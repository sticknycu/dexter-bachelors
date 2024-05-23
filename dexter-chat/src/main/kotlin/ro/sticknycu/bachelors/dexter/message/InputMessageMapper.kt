package ro.sticknycu.bachelors.dexter.message

import org.springframework.stereotype.Service
import ro.sticknycu.bachelors.dexter.chats.api.InputMessage
import java.time.Clock
import java.time.LocalDateTime

@Service
class InputMessageMapper(private val clock: Clock) {
    fun fromInput(inputMessage: InputMessage): MessageDocument {
        val messageTime = clock.instant();
        return MessageDocument(usernameFrom = inputMessage.usernameFrom, content = inputMessage.content, chatRoomId = inputMessage.chatRoomId, timestamp = messageTime)
    }
}
