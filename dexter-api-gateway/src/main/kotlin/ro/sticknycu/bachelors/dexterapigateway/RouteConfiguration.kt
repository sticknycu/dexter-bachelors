package ro.sticknycu.bachelors.dexterapigateway

import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
class RouteConfiguration {

//    @Bean
//    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
//        return builder.routes()
//            // Route for /blocking endpoint
//            .route("blocking_route") { r: PredicateSpec ->
//                r.path("/blocking/**")
//                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
//                    .uri("http://localhost:9091/blocking")
//            }
//            // Route for /reactive endpoint
//            .route("reactive_route") { r: PredicateSpec ->
//                r.path("/reactive/**")
//                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
//                    .uri("http://localhost:9090/reactive")
//            }
//            .build()
//    }
}