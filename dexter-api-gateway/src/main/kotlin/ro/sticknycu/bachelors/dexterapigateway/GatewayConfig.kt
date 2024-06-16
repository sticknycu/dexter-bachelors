import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.switchIfEmpty

@Configuration
class GatewayConfig {

    @Bean
    fun modifyRequestBodyGatewayFilterFactory(): ModifyRequestBodyGatewayFilterFactory {
        return ModifyRequestBodyGatewayFilterFactory()
    }

    @Bean
    fun customGlobalFilter(modifyRequestBodyGatewayFilterFactory: ModifyRequestBodyGatewayFilterFactory): GlobalFilter {
        return GlobalFilter { exchange, chain ->
            ReactiveSecurityContextHolder.getContext()
                .map { it.authentication.principal }
                .cast(Jwt::class.java)
                .flatMap { jwt ->
                    val username = jwt.getClaimAsString("preferred_username")
                    val modifiedExchange = exchange.mutate().request(object : ServerHttpRequestDecorator(exchange.request) {
                        override fun getBody(): Flux<DataBuffer> {
                            return DataBufferUtils.join(super.getBody())
                                .flatMapMany { dataBuffer ->
                                    val bytes = ByteArray(dataBuffer.readableByteCount())
                                    dataBuffer.read(bytes)
                                    DataBufferUtils.release(dataBuffer)
                                    val body = String(bytes)

                                    // Assuming the body is JSON
                                    val modifiedBody = body.replaceFirst("\\{".toRegex(), "{ \"username\": \"$username\", ")
                                    val newBytes = modifiedBody.toByteArray()

                                    val bufferFactory = exchange.response.bufferFactory()
                                    Flux.just(bufferFactory.wrap(newBytes))
                                }
                        }
                    }).build()

                    chain.filter(modifiedExchange)
                }.switchIfEmpty { chain.filter(exchange) }
        }
    }
}
