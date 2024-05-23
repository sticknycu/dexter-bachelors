package ro.sticknycu.bachelors.dexter.chats.api

import ro.sticknycu.bachelors.dexter.message.MessageDocument
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class InputMessage(val usernameFrom: String, val content: String, val chatRoomId: UUID)

data class Message(val usernameFrom: String, val content: String, val chatRoomId: UUID, val time: Instant)

data class Page(val pageNumber: Int, val pageSize: Int)

 fun fromMessageDocument(messageDocument: MessageDocument): Message {
        return Message(
            messageDocument.usernameFrom,
            messageDocument.content,
            messageDocument.chatRoomId,
            messageDocument.timestamp
        )
    }