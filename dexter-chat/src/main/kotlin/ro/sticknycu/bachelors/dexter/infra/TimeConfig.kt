package ro.sticknycu.bachelors.dexter.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration
class TimeConfig {
    @Bean
    fun clock(): Clock {
        return Clock.system(ZoneId.of("UTC"))
    }
}
