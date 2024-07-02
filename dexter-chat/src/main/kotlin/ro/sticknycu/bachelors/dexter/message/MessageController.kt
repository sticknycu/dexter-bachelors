package ro.sticknycu.bachelors.dexter.message

import kotlinx.coroutines.reactor.mono
import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
//import org.springframework.messaging.handler.annotation.MessageMapping
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ro.sticknycu.bachelors.dexter.chats.api.InputMessage
import ro.sticknycu.bachelors.dexter.chats.api.Message
import ro.sticknycu.bachelors.dexter.chats.image.ImageGenerationService
import ro.sticknycu.bachelors.dexter.chats.image.ImageGeneratorGrpcClient
import java.io.ByteArrayInputStream
import java.nio.file.Path
import java.util.Base64


@RestController
class MessageController(
    @param:Qualifier("mongoChatToUserMappingsHolder") private val chatRoomUserMappings: ChatToUserMappingsHolder,
    private val messageRepository: MessageRepository,
    private val imageGeneratorGrpcClient: ImageGeneratorGrpcClient,
    private val newMessageWatcher: NewMessageWatcher, private val inputMessageMapper: InputMessageMapper
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

    @PostMapping("/send-message")
    fun handle(@RequestBody inputMessage: InputMessage, @RequestParam("username") username: String): Mono<Message?> {
        logger.info("InputMessage: $inputMessage, username: $username")
//        inputMessage.content = Base64.getDecoder().decode(inputMessage.content).toString()
          return messageRepository.save(inputMessageMapper.fromInput(inputMessage, username, null, null))
              .then(Mono.defer {
                  imageGeneratorGrpcClient.generateImage(inputMessage.content.toByteArray())
                      .flatMap { imageMap ->
                          val messageDocument = inputMessageMapper.fromInput(
                              inputMessage,
                              username,
                              imageMap["canny_edge"]!!,
                              imageMap["generated_image"]!!
                          )
                          messageDocument.usernameFrom = "Dexter"
                          messageRepository.save(messageDocument)
                              .map {
                                  MessageMapper.fromMessageDocument(it)
                              }
                      }.doOnSuccess {
                          messageRepository.save(inputMessageMapper.fromInput(inputMessage, username, null, null))
                              .subscribeOn(Schedulers.boundedElastic()).subscribe()
                      }
              })
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


class ByteArrayFilePart(
    private val byteArray: ByteArray,
    private val fileName: String
) : FilePart {

    override fun name(): String {
        return "file"
    }

    override fun filename(): String {
        return fileName
    }

    override fun headers() = HttpHeaders()

    override fun content(): Flux<DataBuffer> {
        val bufferFactory = DefaultDataBufferFactory()
        val buffer: DataBuffer = bufferFactory.wrap(byteArray)
        return Flux.just(buffer)
    }

    override fun transferTo(dest: Path): Mono<Void> {
        return Mono.empty()
    }
}

fun byteArrayToFilePart(byteArray: ByteArray, fileName: String): FilePart {
    return ByteArrayFilePart(byteArray, fileName)
}