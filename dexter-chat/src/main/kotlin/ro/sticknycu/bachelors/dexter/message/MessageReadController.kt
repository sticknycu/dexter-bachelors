package ro.sticknycu.bachelors.dexter.message

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.sticknycu.bachelors.dexter.chats.api.Message
import ro.sticknycu.bachelors.dexter.chats.api.Page
import java.util.*

@Controller
class MessageReadController(private val messageService: MessageService) {

    @MessageMapping("chat.{chatId}.messages")
    fun getMessagesForChatUnpaged(@DestinationVariable("chatId") chatId: UUID): Flux<Message> {
        return messageService.findByChatId(chatId)
    }

    @MessageMapping("chat.{chatId}.messages.single")
    fun getMessagesForChatAsSingle(@DestinationVariable("chatId") chatId: UUID): Mono<List<Message>> {
        return messageService.findByChatId(chatId).collectList()
    }

    @MessageMapping("chat.{chatId}.messages.paged")
    fun getMessagesForChatPaged(@DestinationVariable("chatId") chatId: UUID, page: Page): Flux<Message> {
        return messageService.findByChatId(chatId, page)
    }

    @MessageMapping("chat.{chatId}.messages.paged.single")
    fun getMessagesForChatPagedSingle(@DestinationVariable("chatId") chatId: UUID, page: Page): Mono<List<Message>> {
        return messageService.findByChatId(chatId, page).collectList()
    }
}
