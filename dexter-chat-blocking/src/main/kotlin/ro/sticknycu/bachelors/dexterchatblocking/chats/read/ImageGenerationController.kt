package ro.sticknycu.bachelors.dexterchatblocking.chats.read

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

@RestController
class ImageGenerationController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ImageGenerationController::class.java)
    }

    @GetMapping("/test")
    fun test(): String {
        logger.info("Called blocking!")
        return "Ok!"
    }
}

@RestController
class MessageController(
    private val messageRepository: MessageRepository,
    private val imageGeneratorGrpcClient: ImageGeneratorGrpcClient,
    private val inputMessageMapper: InputMessageMapper
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageController::class.java)
    }

//    @MessageMapping("chat-channel")
//    fun handle(incomingMessages: Flux<InputMessage>, @AuthenticationPrincipal jwtMono: Mono<Jwt>): Flux<Message> {
//        val messages = incomingMessages.map { inputMessage: InputMessage -> inputMessageMapper.fromInput(inputMessage) }
//        val incomingMessagesSubscription = messageRepository.saveAll(messages)
//            .then()
//            .subscribeOn(Schedulers.boundedElastic())
//            .subscribe()
//        val userNameMono = jwtMono.map { obj: Jwt -> JwtUtil.extractUserName(obj) }
//        val userChats: Mono<Set<UUID>> = chatRoomUserMappings.getUserChatRooms(userNameMono)
//        return newMessageWatcher.newMessagesForChats(userChats, userNameMono)
//            .doOnNext { message: Message? -> logger.info("Message reply {}", message) }
//            .doOnCancel { incomingMessagesSubscription.dispose() }
//            .doOnError { throwable: Throwable -> logger.error(throwable.message) }
//    }

    @PostMapping("/process-image", consumes = ["*/*"])
    fun processImage(@RequestPart("file") file: FilePart): ResponseEntity<Map<String, ByteArray>> =
        imageGenerationService.processImage(file)

    @PostMapping("/send-message")
    suspend fun handle(@RequestBody inputMessage: InputMessage, @RequestParam("username") username: String): Message? {
        logger.info("InputMessage: $inputMessage, username: $username")
//        inputMessage.content = Base64.getDecoder().decode(inputMessage.content).toString()

        withContext(Dispatchers.IO) {
            messageRepository.save(inputMessageMapper.fromInput(inputMessage, username, null, null))
        }

        val imageMap = imageGeneratorGrpcClient.generateImage(inputMessage.content.toByteArray())

        val messageDocument = inputMessageMapper.fromInput(
                              inputMessage,
                              username,
                              imageMap["canny_edge"]!!,
                              imageMap["generated_image"]!!
                          )
        messageDocument.usernameFrom = "Dexter"

        withContext(Dispatchers.IO) {
            messageRepository.save(inputMessageMapper.fromInput(inputMessage, username, null, null))
        }

        return MessageMapper.fromMessageDocument(withContext(Dispatchers.IO) {
            messageRepository.save(messageDocument)
        })

//        return
//
//          return messageRepository.save(inputMessageMapper.fromInput(inputMessage, username, null, null))
//              .then(Mono.defer {
//                  imageGeneratorGrpcClient.generateImage(inputMessage.content.toByteArray())
//                      .flatMap { imageMap ->
//                          val messageDocument = inputMessageMapper.fromInput(
//                              inputMessage,
//                              username,
//                              imageMap["canny_edge"]!!,
//                              imageMap["generated_image"]!!
//                          )
//                          messageDocument.usernameFrom = "Dexter"
//                          messageRepository.save(messageDocument)
//                              .map {
//                                  MessageMapper.fromMessageDocument(it)
//                              }
//                      }.doOnSuccess {
//                          messageRepository.save(inputMessageMapper.fromInput(inputMessage, username, null, null))
//                              .subscribeOn(Schedulers.boundedElastic()).subscribe()
//                      }
//              })
    }

//    @MessageMapping("messages-stream")
//    fun handle(@AuthenticationPrincipal jwtMono: Mono<Jwt>): Flux<Message> {
//        val userName = jwtMono.doOnNext { jwt: Jwt -> logger.info(jwt.claims.toString()) }
//            .map { obj: Jwt -> JwtUtil.extractUserName(obj) }
//            .doOnNext { s: String -> logger.info("username {}", s) }
//        val userChats: Mono<Set<UUID>> = chatRoomUserMappings.getUserChatRooms(userName)
//        return newMessageWatcher.newMessagesForChats(userChats, userName)
//            .doOnNext { message: Message -> logger.info("Message reply {}", message) }
//            .doOnError { throwable: Throwable -> logger.error(throwable.message) }
//    }
}


@Service
class InputMessageMapper {
    fun fromInput(inputMessage: InputMessage, username: String, cannyImage: ByteArray?, generatedImage: ByteArray?): MessageDocument {
        val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val messageTime = clock.instant();
        return MessageDocument(usernameFrom = username, cannyImage = cannyImage, originalImage = inputMessage.content, generatedImage = generatedImage, chatRoomId = inputMessage.chatRoomId, timestamp = messageTime)
    }
}


data class InputMessage(var content: String, val chatRoomId: UUID)

data class Message(val usernameFrom: String, val originalImage: String?, val cannyImage: ByteArray?, val generatedImage: ByteArray?, val chatRoomId: UUID, val time: Instant)

data class Page(val pageNumber: Int, val pageSize: Int)

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

@Service
class ImageGenerationService {
        fun processImage(file: FilePart): ResponseEntity<Map<String, ByteArray>> =
        ResponseEntity.ok(file.content()
            .map { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(byteArray)
                byteArray
            }
            .publishOn(Schedulers.boundedElastic())
            .reduce { byteArray1, byteArray2 ->
                val outputStream = ByteArrayOutputStream()
                outputStream.write(byteArray1)
                outputStream.write(byteArray2)
                outputStream.toByteArray()
            }.flatMap {
                imageGeneratorGrpcClient.generateImage(it)
            })
}