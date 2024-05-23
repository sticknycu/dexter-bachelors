package ro.sticknycu.bachelors.dexter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication()
class DexterChatApplication

fun main(args: Array<String>) {
    runApplication<DexterChatApplication>(*args)
}
