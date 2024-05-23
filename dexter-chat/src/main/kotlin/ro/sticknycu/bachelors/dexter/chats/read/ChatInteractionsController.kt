package ro.sticknycu.bachelors.dexter.chats.read

import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
import ro.sticknycu.bachelors.dexter.chats.api.ChatCreatedResponse
import ro.sticknycu.bachelors.dexter.chats.api.JoinChatRequest
import ro.sticknycu.bachelors.dexter.common.JwtUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.*

@Controller
class ChatInteractionsController(private val chatRoomUserMappings: ChatToUserMappingsHolder) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChatInteractionsController::class.java)
    }

    @MessageMapping("create-chat")
    fun createChat(join: String, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<ChatCreatedResponse> {
        logger.info("Creating new chat")
        val chatId = UUID.randomUUID()
        return chatRoomUserMappings.putUserToChat(jwtMono.map { jwt: Jwt -> JwtUtil.extractUserName(jwt) }
            .doOnNext { s: String -> logger.info("username {}", s) }, chatId)
            .log()
            .map { ChatCreatedResponse(chatId) }
            .doOnNext { chatCreatedResponse: ChatCreatedResponse ->
                logger.info(
                    chatCreatedResponse.chatId.toString()
                )
            }
    }

    @MessageMapping("join-chat")
    fun joinChat(joinChatRequest: JoinChatRequest, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Boolean> {
        return chatRoomUserMappings.putUserToChat(
            jwtMono.map { jwt: Jwt -> JwtUtil.extractUserName(jwt) },
            joinChatRequest.chatId
        )
            .log()
    }
}
