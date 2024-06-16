package ro.sticknycu.bachelors.dexter.chats.read

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
//import org.springframework.messaging.handler.annotation.MessageMapping
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
class ChatReadController(private val chatToUserMappingsHolder: ChatToUserMappingsHolder) {

    companion object {
        private val logger = LoggerFactory.getLogger(ChatReadController::class.java)
    }

    @GetMapping("/user-chats")
    fun getUserChats(@RequestParam("username") username: String): Mono<Set<UUID>> {
        logger.info("Received request with username: $username")
        return chatToUserMappingsHolder.getUserChatRooms(username)
    }
}

