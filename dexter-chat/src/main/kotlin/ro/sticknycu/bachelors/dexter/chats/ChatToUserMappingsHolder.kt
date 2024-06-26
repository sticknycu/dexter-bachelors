package ro.sticknycu.bachelors.dexter.chats

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ChatToUserMappingsHolder {
    fun putUserToChat(userName: String, chatId: UUID): Mono<Boolean>

    fun getUserChatRooms(userName: String): Mono<Set<UUID>>

    fun clear(): Flux<UsernameToChatsDocument>
}
