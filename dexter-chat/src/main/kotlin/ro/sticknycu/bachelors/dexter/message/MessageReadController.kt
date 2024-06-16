package ro.sticknycu.bachelors.dexter.message

//import org.springframework.messaging.handler.annotation.DestinationVariable
//import org.springframework.messaging.handler.annotation.MessageMapping
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.sticknycu.bachelors.dexter.chats.api.Message
import ro.sticknycu.bachelors.dexter.chats.api.Page
import java.util.*

@RestController
class MessageReadController(private val messageService: MessageService) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageReadController::class.java);
    }

    @GetMapping("/chat/{chatId}/messages")
    fun getMessagesForChatUnpaged(@PathVariable("chatId") chatId: UUID, @RequestParam("username") username: String): Flux<Message> {
        logger.info("Username $username get messages for chat $chatId")
        return messageService.findByChatId(chatId)
    }

    @GetMapping("/chat/{chatId}/messages/single")
    fun getMessagesForChatAsSingle(@PathVariable("chatId") chatId: UUID): Mono<List<Message>> {
        return messageService.findByChatId(chatId).collectList()
    }

    @GetMapping("/chat/{chatId}/messages/paged")
    fun getMessagesForChatPaged(@PathVariable("chatId") chatId: UUID, page: Page): Flux<Message> {
        return messageService.findByChatId(chatId, page)
    }

    @GetMapping("/chat/{chatId}.messages.paged.single")
    fun getMessagesForChatPagedSingle(@PathVariable("chatId") chatId: UUID, page: Page): Mono<List<Message>> {
        return messageService.findByChatId(chatId, page).collectList()
    }
}
