package ro.sticknycu.bachelors.dexter.infra

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class RequestLoggingFilter : WebFilter {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Log request details
        logger.info("Request Method: ${exchange.request.method}")
        logger.info("Request URI: ${exchange.request.uri}")
        logger.info("Request Headers: ${exchange.request.headers}")
        return chain.filter(exchange)
    }
}