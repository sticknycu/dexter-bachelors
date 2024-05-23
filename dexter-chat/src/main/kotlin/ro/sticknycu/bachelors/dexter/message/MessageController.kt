package ro.sticknycu.bachelors.dexter.message

import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ro.sticknycu.bachelors.dexter.chats.api.InputMessage
import ro.sticknycu.bachelors.dexter.chats.api.Message
import ro.sticknycu.bachelors.dexter.common.JwtUtil
import java.util.*

@Controller
class MessageController(
    @param:Qualifier("mongoChatToUserMappingsHolder") private val chatRoomUserMappings: ChatToUserMappingsHolder,
    private val messageRepository: MessageRepository,
    private val newMessageWatcher: NewMessageWatcher, private val inputMessageMapper: InputMessageMapper
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageController::class.java)
    }

    @MessageMapping("chat-channel")
    fun handle(incomingMessages: Flux<InputMessage>, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Flux<Message> {
        val messages = incomingMessages.map { inputMessage: InputMessage -> inputMessageMapper.fromInput(inputMessage) }
        val incomingMessagesSubscription = messageRepository.saveAll(messages)
            .then()
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
        val userNameMono = jwtMono.map { obj: Jwt -> JwtUtil.extractUserName(obj) }
        val userChats: Mono<Set<UUID>> = chatRoomUserMappings.getUserChatRooms(userNameMono)
        return newMessageWatcher.newMessagesForChats(userChats, userNameMono)
            .doOnNext { message: Message? -> logger.info("Message reply {}", message) }
            .doOnCancel { incomingMessagesSubscription.dispose() }
            .doOnError { throwable: Throwable -> logger.error(throwable.message) }
    }

    @MessageMapping("send-message")
    fun handle(inputMessage: InputMessage, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Message?> {
        val messageDocument = inputMessageMapper.fromInput(inputMessage)
        return messageRepository.save(messageDocument)
            .map { obj: MessageDocument -> MessageMapper.fromMessageDocument(obj) }
    }

    @MessageMapping("messages-stream")
    fun handle(@AuthenticationPrincipal jwtMono: Mono<Jwt>): Flux<Message> {
        val userName = jwtMono.doOnNext { jwt: Jwt -> logger.info(jwt.claims.toString()) }
            .map { obj: Jwt -> JwtUtil.extractUserName(obj) }
            .doOnNext { s: String -> logger.info("username {}", s) }
        val userChats: Mono<Set<UUID>> = chatRoomUserMappings.getUserChatRooms(userName)
        return newMessageWatcher.newMessagesForChats(userChats, userName)
            .doOnNext { message: Message -> logger.info("Message reply {}", message) }
            .doOnError { throwable: Throwable -> logger.error(throwable.message) }
    }
}
