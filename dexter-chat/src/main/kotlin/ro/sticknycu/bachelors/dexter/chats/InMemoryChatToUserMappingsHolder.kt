package ro.sticknycu.bachelors.dexter.chats

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryChatToUserMappingsHolder : ChatToUserMappingsHolder {
    private val userNameToChat: MutableMap<String, MutableSet<UUID>> = ConcurrentHashMap()

    override fun putUserToChat(userName: Mono<String>, chatId: UUID): Mono<Boolean> {
        return userName.map { s: String ->
            userNameToChat.computeIfAbsent(s) { HashSet() }
                .add(chatId)
        }
    }

    override fun getUserChatRooms(userName: Mono<String>): Mono<Set<UUID>> {
        return userName.mapNotNull { key: String -> userNameToChat[key] }
    }

    override fun clear(): Flux<UsernameToChatsDocument> {
        userNameToChat.clear()
        return Flux.empty()
    }
}
