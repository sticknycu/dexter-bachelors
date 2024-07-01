package ro.sticknycu.bachelors.dexter.chats.image

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.util.*


@Service
class ImageGenerationService(
    private val imageGeneratorGrpcClient: ImageGeneratorGrpcClient
) {

    fun processImage(file: FilePart): ResponseEntity<Mono<Map<String, ByteArray>>> {

        return ResponseEntity.ok(DataBufferUtils.join(file.content())
            .map { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(byteArray)
                DataBufferUtils.release(dataBuffer)
                byteArray
            }
            .flatMap { byteArray ->
                val base64EncodedString = Base64.getEncoder().encodeToString(byteArray)
                imageGeneratorGrpcClient.generateImage(base64EncodedString.toByteArray())
            })
    }
//        ResponseEntity.ok(file.content()
//            .map { dataBuffer ->
//                val byteArray = ByteArray(dataBuffer.readableByteCount())
//                dataBuffer.read(byteArray)
//                byteArray
//            }
//            .publishOn(Schedulers.boundedElastic())
//            .reduce { byteArray1, byteArray2 ->
//                val outputStream = ByteArrayOutputStream()
//                outputStream.write(byteArray1)
//                outputStream.write(byteArray2)
//                outputStream.toByteArray()
//            }.flatMap {
//                imageGeneratorGrpcClient.generateImage(it)
//            })
}