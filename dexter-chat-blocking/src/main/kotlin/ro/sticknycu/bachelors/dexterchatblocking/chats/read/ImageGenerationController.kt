package ro.sticknycu.bachelors.dexterchatblocking.chats.read

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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