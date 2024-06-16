package ro.sticknycu.bachelors.dexter.chats

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryChatToUserMappingsHolder : ChatToUserMappingsHolder {
    private val userNameToChat: MutableMap<String, MutableSet<UUID>> = ConcurrentHashMap()

    override fun putUserToChat(userName: String, chatId: UUID): Mono<Boolean> {
        return Mono.just(userNameToChat.computeIfAbsent(userName) { HashSet() }
            .add(chatId))
    }

    override fun getUserChatRooms(userName: String): Mono<Set<UUID>> {
        return userNameToChat[userName]?.toSet()?.let { Mono.just(it) } ?: Mono.just(setOf())
    }

    override fun clear(): Flux<UsernameToChatsDocument> {
        userNameToChat.clear()
        return Flux.empty()
    }
}
