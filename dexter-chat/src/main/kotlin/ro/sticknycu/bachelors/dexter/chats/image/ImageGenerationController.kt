package ro.sticknycu.bachelors.dexter.chats.image

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
class ImageGenerationController(private val imageGenerationService: ImageGenerationService) {

    @PostMapping("/process-image", consumes = ["*/*"])
    fun processImage(@RequestPart("file") file: FilePart): ResponseEntity<Mono<Map<String, ByteArray>>> =
        imageGenerationService.processImage(file)
}