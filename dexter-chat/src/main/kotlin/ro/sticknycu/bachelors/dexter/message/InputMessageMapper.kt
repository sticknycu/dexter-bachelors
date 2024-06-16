package ro.sticknycu.bachelors.dexter.message

import org.bson.BsonBinary
import org.springframework.stereotype.Service
import ro.sticknycu.bachelors.dexter.chats.api.InputMessage
import java.time.Clock
import java.time.LocalDateTime

@Service
class InputMessageMapper(private val clock: Clock) {
    fun fromInput(inputMessage: InputMessage, username: String, cannyImage: ByteArray?, generatedImage: ByteArray?): MessageDocument {
        val messageTime = clock.instant();
        return MessageDocument(usernameFrom = username, cannyImage = cannyImage, originalImage = inputMessage.content, generatedImage = generatedImage, chatRoomId = inputMessage.chatRoomId, timestamp = messageTime)
    }
}
