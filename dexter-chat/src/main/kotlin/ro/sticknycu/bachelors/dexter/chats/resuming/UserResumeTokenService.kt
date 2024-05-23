package ro.sticknycu.bachelors.dexter.chats.resuming

import org.bson.BsonTimestamp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Clock
import java.util.function.Function

@Service
class UserResumeTokenService(
    private val userResumeTokenRepository: UserResumeTokenRepository,
    private val clock: Clock
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun saveAndGenerateNewTokenFor(userName: String) {
       logger.info("Saving token for user {}", userName)
        val userToken = userResumeTokenRepository.findByUserName(userName)
            .defaultIfEmpty(UserResumeTokenDocument(userName = userName))
            .map(changeCurrentToken())
        userResumeTokenRepository.saveAll(userToken)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnComplete { logger.info("User {} token saved ", userName) }
            .subscribe()
    }

    private fun changeCurrentToken(): Function<UserResumeTokenDocument, UserResumeTokenDocument> {
        return Function { userResumeTokenDocument: UserResumeTokenDocument ->
            val epochSecond = clock.instant().epochSecond
            userResumeTokenDocument.setTokenTimestamp(BsonTimestamp(epochSecond.toInt(), 0))
            userResumeTokenDocument
        }
    }

    fun getResumeTimestampFor(userNameMono: Mono<String>): Mono<BsonTimestamp> {
        return userNameMono.flatMap { userName ->
            userResumeTokenRepository.findByUserName(userName)
                .map { obj -> obj.tokenTimestamp }
                .defaultIfEmpty(BsonTimestamp(clock.instant().epochSecond.toInt(), 0))
        }
    }

    fun deleteTokenForUser(username: String): Mono<Boolean> {
        return userResumeTokenRepository.deleteByUserName(username)
            .map { deletedCount: Long? -> true }
            .doOnSuccess { logger.info("Token for user {} deleted", username) }
    }
}
