package ro.sticknycu.bachelors.dexter.message

import ro.sticknycu.bachelors.dexter.chats.api.InputMessage
import ro.sticknycu.bachelors.dexter.chats.api.Message


object MessageMapper {
    @JvmStatic
    fun fromMessageDocument(messageDocument: MessageDocument): Message {
        return Message(
            messageDocument.usernameFrom,
            messageDocument.originalImage,
            messageDocument.cannyImage,
            messageDocument.generatedImage,
            messageDocument.chatRoomId,
            messageDocument.timestamp
        )
    }
}
