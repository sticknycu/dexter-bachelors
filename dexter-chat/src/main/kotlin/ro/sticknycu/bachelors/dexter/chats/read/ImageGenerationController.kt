package ro.sticknycu.bachelors.dexter.chats.read

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ImageGenerationController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ImageGenerationController::class.java)
    }

    @GetMapping("/test")
    fun test(): Mono<String> {
        logger.info("Called!")
        return Mono.just("Ok!")
    }
}