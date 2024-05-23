package ro.sticknycu.bachelors.dexter.message

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ro.sticknycu.bachelors.dexter.chats.api.Message
import ro.sticknycu.bachelors.dexter.chats.api.Page
import java.util.*

@Service
class MessageService(private val messageRepository: MessageRepository) {
    //    @PreAuthorize("@permissionEvaluator.isUserPartOfChat(#chatId)")
    fun findByChatId(chatId: UUID): Flux<Message> {
        return messageRepository.findByChatRoomId(chatId, byTimestampDesc())
            .map { obj -> MessageMapper.fromMessageDocument(obj) }
    }

    //    @PreAuthorize("@permissionEvaluator.isUserPartOfChat(#chatId)")
    fun findByChatId(chatId: UUID, page: Page): Flux<Message> {
        return messageRepository.findByChatRoomId(chatId, byTimestampDesc())
            .map { obj: MessageDocument -> MessageMapper.fromMessageDocument(obj) }
    }

    private fun byTimestampDesc(): Sort {
        return Sort.by(Sort.Direction.DESC, "timestamp")
    }
}
