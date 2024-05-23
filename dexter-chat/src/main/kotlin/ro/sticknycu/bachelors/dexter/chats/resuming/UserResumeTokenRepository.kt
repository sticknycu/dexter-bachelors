package ro.sticknycu.bachelors.dexter.chats.resuming

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Repository
@Transactional
interface UserResumeTokenRepository : ReactiveMongoRepository<UserResumeTokenDocument, UUID> {
    fun findByUserName(username: String): Mono<UserResumeTokenDocument>
    fun deleteByUserName(username: String): Mono<Long>
}
