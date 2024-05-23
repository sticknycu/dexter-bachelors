package ro.sticknycu.bachelors.dexter.chats.read

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
import ro.sticknycu.bachelors.dexter.common.JwtUtil
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@Controller
class ChatReadController(private val chatToUserMappingsHolder: ChatToUserMappingsHolder) {

    @MessageMapping("get-user-chats")
    fun getUserChats(@AuthenticationPrincipal jwtMono: Mono<Jwt>): Mono<Set<UUID>> {
        return chatToUserMappingsHolder.getUserChatRooms(jwtMono.map { jwt: Jwt -> JwtUtil.extractUserName(jwt) })
    }
}

