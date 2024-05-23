package ro.sticknycu.bachelors.dexterapigateway

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController {

    @RequestMapping("/circuitbreakerfallback")
	fun circuitbreakerfallback(): String {
		return "This is a fallback"
	}

}