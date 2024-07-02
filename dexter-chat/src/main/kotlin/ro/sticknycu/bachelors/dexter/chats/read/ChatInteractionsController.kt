package ro.sticknycu.bachelors.dexter.chats.read

import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
import ro.sticknycu.bachelors.dexter.chats.api.ChatCreatedResponse
import ro.sticknycu.bachelors.dexter.chats.api.JoinChatRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*


@RestController
class ChatInteractionsController(private val chatRoomUserMappings: ChatToUserMappingsHolder) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChatInteractionsController::class.java)
    }

    @PostMapping("/chat")
    fun createChat(@RequestParam username: String): Mono<ChatCreatedResponse> {
        logger.info("Creating new chat for username $username")
        val chatId = UUID.randomUUID()
        logger.info("")
        return chatRoomUserMappings.putUserToChat(username, chatId)
            .map { ChatCreatedResponse(chatId) }
            .doOnNext { chatCreatedResponse: ChatCreatedResponse ->
                logger.info(
                    chatCreatedResponse.chatId.toString()
                )
            }
    }

//    @MessageMapping("join-chat")
//    fun joinChat(joinChatRequest: JoinChatRequest, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Boolean> {
//        return chatRoomUserMappings.putUserToChat(
//            jwtMono.map { jwt: Jwt -> JwtUtil.extractUserName(jwt) },
//            joinChatRequest.chatId
//        )
//            .log()
//    }
}
