package ro.sticknycu.bachelors.dexter.chats.api

import ro.sticknycu.bachelors.dexter.message.MessageDocument
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class InputMessage(var content: String, val chatRoomId: UUID)

data class Message(val usernameFrom: String, val originalImage: String?, val cannyImage: ByteArray?, val generatedImage: ByteArray?, val chatRoomId: UUID, val time: Instant)

data class Page(val pageNumber: Int, val pageSize: Int)
