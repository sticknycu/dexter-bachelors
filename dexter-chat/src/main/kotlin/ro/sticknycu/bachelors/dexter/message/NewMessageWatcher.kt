package ro.sticknycu.bachelors.dexter.message

import com.mongodb.client.model.changestream.OperationType
import ro.sticknycu.bachelors.dexter.chats.resuming.UserResumeTokenService
import org.bson.BsonTimestamp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.sticknycu.bachelors.dexter.chats.api.Message
import java.util.*

@Component
class NewMessageWatcher(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val resumeTokenService: UserResumeTokenService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NewMessageWatcher::class.java)
    }

    fun newMessagesForChats(chats: Mono<Set<UUID>>, userNameMono: Mono<String>): Flux<Message> {
        return userNameMono.flatMapMany { userName: String ->
            resumeTokenService.getResumeTimestampFor(userNameMono)
                .flatMapMany { bsonTimestamp: BsonTimestamp -> changeStream(userName, chats, bsonTimestamp) }
                .doOnCancel { resumeTokenService.saveAndGenerateNewTokenFor(userName) }
        }
    }

    private fun changeStream(
        username: String,
        chats: Mono<Set<UUID>>,
        bsonTimestamp: BsonTimestamp
    ): Flux<Message> {
        return reactiveMongoTemplate.changeStream(MessageDocument::class.java)
            .watchCollection("messages")
            .resumeAt(bsonTimestamp)
            .listen()
            .doOnNext { e: ChangeStreamEvent<MessageDocument> -> logger.info(" xdd event {}", e) }
            .filter { event: ChangeStreamEvent<MessageDocument> -> event.operationType == OperationType.INSERT }
            .mapNotNull { obj: ChangeStreamEvent<MessageDocument> -> obj.body!! }
            .doOnNext { messageDocument: MessageDocument -> logger.info(messageDocument.toString()) }
            .filter { m: MessageDocument -> m.isNotFromUser(username) }
            .filterWhen {
                chats.map { chatIds: Set<UUID> ->
                    logger.info(" chatids $chatIds")
                    chatIds.contains(it.chatRoomId)
                }
            }
            .map { obj: MessageDocument -> MessageMapper.fromMessageDocument(obj) }
    }
}
