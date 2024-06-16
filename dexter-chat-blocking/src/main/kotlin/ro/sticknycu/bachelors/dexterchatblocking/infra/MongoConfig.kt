package ro.sticknycu.bachelors.dexter.infra

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.UuidRepresentation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@Configuration
@EnableMongoRepositories(basePackages = [
        "ro.sticknycu.bachelors.dexter"
])
class MongoDBCConfig : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return "chatsblocking"
    }

    @Bean
    override fun mongoClient(): MongoClient {
        return MongoClients.create(MongoClientSettings.builder()
               .uuidRepresentation(UuidRepresentation.STANDARD)
               .applyConnectionString(ConnectionString("mongodb://root:password@mongodb:27017/chats?replicaSet=rs0&retryWrites=true&w=majority"))
//               .applyToClusterSettings { builder: ClusterSettings.Builder ->
//                   builder.requiredReplicaSetName("rs0")
////                   builder.requiredClusterType(ClusterType.REPLICA_SET)
//               }
               .build()
        )
    }

    @Bean
    fun transactionManager(mongoDatabaseFactory: MongoDatabaseFactory?): MongoTransactionManager {
        return MongoTransactionManager(mongoDatabaseFactory!!)
    }

//    @Bean
//    override fun reactiveMongoDbFactory(): ReactiveMongoDatabaseFactory {
//        return SimpleReactiveMongoDatabaseFactory(reactiveMongoClient(), "chats")
//    }

    @Bean
    fun mongoEventListener(): LoggingEventListener {
        return LoggingEventListener()
    }

//    @Autowired
//    lateinit var reactiveMongoDbFactory: ReactiveMongoDatabaseFactory
//
//    @Autowired
//    lateinit var mongoMappingContext: MongoMappingContext

//    @Bean
//    @Primary
//    fun mongoMappingConverter(): MappingMongoConverter {
//        val mappingConverter =
//            MappingMongoConverter(DefaultDbRefResolver(reactiveMongoDbFactory), mongoMappingContext)
//        // Make sure to register your custom converters here
//        mappingConverter.setCustomConversions(customConversions())
//        return mappingConverter
//    }

//    override fun customConversions(): MongoCustomConversions {
//        return MongoCustomConversions(
//            asList( // writing converter, reader converter
//                BinaryBsonToUUID(), UUIDToBinaryBson()
//            )
//        )
//    }
//
//    @WritingConverter
//    class BinaryBsonToUUID : org.springframework.core.convert.converter.Converter<Binary?, UUID?> {
//
//        override fun convert(source: Binary): UUID {
//            return UUID.nameUUIDFromBytes(source.data)
//        }
//    }
//
//    @ReadingConverter
//    class UUIDToBinaryBson : org.springframework.core.convert.converter.Converter<UUID?, Binary?> {
//
//        override fun convert(source: UUID): Binary {
//            val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
//            bb.putLong(source.getMostSignificantBits())
//            bb.putLong(source.getLeastSignificantBits())
//
//            return Binary(bb.array())
//        }
//    }

//    @ReadingConverter
//    class BinaryToUUIDConverter : Converter<Binary?, UUID?> {
//        override fun convert(source: Binary): UUID {
//            return UUID.nameUUIDFromBytes(source.data)
//        }
//    }
//
//    @Bean
//    override fun customConversions(): MongoCustomConversions {
//        val converters: MutableList<Converter<*, *>?> = ArrayList()
//        converters.add(BinaryToUUIDConverter())
//        return MongoCustomConversions(converters)
//    }
}