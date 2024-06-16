package ro.sticknycu.bachelors.dexter.chats

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class MongoChatToUserMappingsHolder(private val reactiveMongoTemplate: ReactiveMongoTemplate) :
    ChatToUserMappingsHolder {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MongoChatToUserMappingsHolder::class.java)
    }

    override fun putUserToChat(userName: String, chatId: UUID): Mono<Boolean> {
        val queryUsername = Query.query(Criteria.where("userName").`is`(userName))
        val document = reactiveMongoTemplate.findOne(queryUsername, UsernameToChatsDocument::class.java)
                .defaultIfEmpty(UsernameToChatsDocument(userName = userName, chats = HashSet()))
                .map { usernameToChatsDocument: UsernameToChatsDocument ->
                    usernameToChatsDocument.addChat(chatId)
                    usernameToChatsDocument
                }

        return reactiveMongoTemplate.save(document)
                .doOnNext { usernameToChatsDocument: UsernameToChatsDocument? ->
                    logger.info(
                        "saving document {}",
                        usernameToChatsDocument
                    )
                }
                .map { usernameToChatsDocument: UsernameToChatsDocument? -> true }
    }

    override fun getUserChatRooms(userName: String): Mono<Set<UUID>> {
        val query = Query.query(Criteria.where("userName").`is`(userName))
        return reactiveMongoTemplate.findOne(query, UsernameToChatsDocument::class.java)
                    .doOnNext { usernameToChatsDocument: UsernameToChatsDocument ->
                        logger.info("found user ${usernameToChatsDocument.userName}")
                    }
                    .map { it.chats.toSet() }
                    .doOnNext { logger.info("set size ${it.size}") }
                    .defaultIfEmpty(setOf())
                    .doOnNext { logger.info("set size ${it.size}") }
    }

    override fun clear(): Flux<UsernameToChatsDocument> {
        return reactiveMongoTemplate.remove(UsernameToChatsDocument::class.java)
            .findAndRemove()
    }
}
