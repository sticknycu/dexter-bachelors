package ro.sticknycu.bachelors.dexter.message

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import ro.sticknycu.bachelors.dexter.message.MessageDocument
import java.util.*

@Repository
@Transactional
interface MessageRepository : ReactiveMongoRepository<MessageDocument, UUID> {
    fun findByChatRoomId(chatRoomId: UUID, pageable: Pageable): Flux<MessageDocument>
    fun findByChatRoomId(chatRoomId: UUID, sort: Sort): Flux<MessageDocument>

    fun findByChatRoomId(chatRoomId: UUID): Flux<MessageDocument>
}
