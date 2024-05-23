package ro.sticknycu.bachelors.dexter.chats.resuming

import org.bson.BsonTimestamp
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document("userTokens")
class UserResumeTokenDocument(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    @field:Indexed(unique = true) val userName: String) {

    private var _tokenTimestamp: BsonTimestamp = BsonTimestamp(Instant.now().epochSecond.toInt(), 0)

    // Public getter
    val tokenTimestamp: BsonTimestamp
        get() = _tokenTimestamp

    // Public setter
    fun setTokenTimestamp(tokenTimestamp: BsonTimestamp) {
        this._tokenTimestamp = tokenTimestamp
    }
}