package ro.sticknycu.bachelors.dexterchatblocking.chats.read

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
interface MessageRepository : MongoRepository<MessageDocument, UUID> {
    fun findByChatRoomId(chatRoomId: UUID, pageable: Pageable): List<MessageDocument>
    fun findByChatRoomId(chatRoomId: UUID, sort: Sort): List<MessageDocument>

    fun findByChatRoomId(chatRoomId: UUID): List<MessageDocument>
}