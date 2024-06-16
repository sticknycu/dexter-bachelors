package ro.sticknycu.bachelors.dexterapigateway

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.net.URI


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class GatewayConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var issuer: String

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GatewayConfig::class.java)
    }


    @Bean
    @Throws(Exception::class)
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.csrf { it.disable() }
//            .cors {
//                it.disable()
//            }
            .authorizeExchange { exchanges: ServerHttpSecurity.AuthorizeExchangeSpec ->
                exchanges
                    .pathMatchers("/**").permitAll()  // Allow all OPTIONS requests
                    .pathMatchers("/reactive/**").authenticated()
                    .pathMatchers("/blocking/**").authenticated()
                    .anyExchange().permitAll()
            }
            .oauth2ResourceServer { oauth ->
                oauth.jwt(Customizer.withDefaults())
            }
            .build()
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.addAllowedOrigin("*") // You can restrict this to specific origins
        corsConfiguration.addAllowedMethod("*") // Add specific methods if needed
        corsConfiguration.addAllowedHeader("*")
        corsConfiguration.allowCredentials = false

        val source: UrlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)

        return CorsWebFilter(source)
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        logger.info("Creating JWT decoder for issuer: $issuer")
        return ReactiveJwtDecoders.fromIssuerLocation(issuer)
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        // Customize the JWT Authentication Converter if necessary
        return converter
    }

}

@Component
class CustomAddRequestParameterFilter : AbstractGatewayFilterFactory<CustomAddRequestParameterFilter.Config>(Config::class.java) {

    class Config {
        // Add any custom configurations if needed
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            exchange.getPrincipal<JwtAuthenticationToken>()
                .cast(JwtAuthenticationToken::class.java)
                .map { auth ->
                    val jwt: Jwt = auth.token
                    val username = jwt.getClaim<String>("preferred_username")
                    val request: ServerHttpRequest = exchange.request.mutate()
                        .uri(addQueryParam(exchange.request.uri, "username", username))
                        .build()
                    exchange.mutate().request(request).build()
                }
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter)
        }
    }

        private fun addQueryParam(uri: URI, name: String, value: String): URI {
        val newQuery = if (uri.query.isNullOrBlank()) {
            "$name=$value"
        } else {
            "${uri.query}&$name=$value"
        }
        return URI(uri.scheme, uri.authority, uri.path, newQuery, uri.fragment)
    }
}