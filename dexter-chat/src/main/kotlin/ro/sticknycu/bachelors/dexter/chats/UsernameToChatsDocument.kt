package ro.sticknycu.bachelors.dexter.chats

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("userChats")
class UsernameToChatsDocument(
    @Id val _id: UUID? = UUID.randomUUID(),
    @field:Indexed(unique = true) val userName: String,
    val chats: MutableSet<UUID>
) {

    fun addChat(chat: UUID) {
        chats.add(chat)
    }
}
