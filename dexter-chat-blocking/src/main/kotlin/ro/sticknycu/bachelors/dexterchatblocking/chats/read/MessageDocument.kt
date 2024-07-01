package ro.sticknycu.bachelors.dexterchatblocking.chats.read

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Document("messages")
class MessageDocument(
    @Id
    val _id: UUID? = UUID.randomUUID(),
    var usernameFrom: String,
    val originalImage: String? = null,
    val cannyImage: ByteArray? = null,
    val generatedImage: ByteArray? = null,
    val chatRoomId: UUID,
//    val saveTime: LocalDateTime = LocalDateTime.now(),
    val timestamp: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)  // Ensure all timestamps are truncated on creation
) {

    fun isNotFromUser(usernameFrom: String?): Boolean {
        return this.usernameFrom != usernameFrom
    }
}