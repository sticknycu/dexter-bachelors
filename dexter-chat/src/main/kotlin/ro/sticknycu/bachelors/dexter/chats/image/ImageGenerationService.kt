package ro.sticknycu.bachelors.dexter.chats.image

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.ByteArrayOutputStream

@Service
class ImageGenerationService(
    private val imageGeneratorGrpcClient: ImageGeneratorGrpcClient
) {

    fun processImage(file: FilePart): ResponseEntity<Mono<Map<String, ByteArray>>> =
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