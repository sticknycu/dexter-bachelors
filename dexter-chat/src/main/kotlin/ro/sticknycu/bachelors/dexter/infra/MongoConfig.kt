package ro.sticknycu.bachelors.dexter.infra

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.UuidRepresentation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(basePackages = [
        "ro.sticknycu.bachelors.dexter"
])
class MongoDBCConfig : AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName(): String {
        return "chats"
    }

    @Bean
    override fun reactiveMongoClient(): MongoClient {
        return MongoClients.create(MongoClientSettings.builder()
               .uuidRepresentation(UuidRepresentation.STANDARD)
               .applyConnectionString(ConnectionString("mongodb://root:password@127.0.0.1:27017,/chats?replicaSet=rs0&authSource=admin&readPreference=primary"))
//               .applyToClusterSettings { builder: ClusterSettings.Builder ->
//                   builder.requiredReplicaSetName("rs0")
////                   builder.requiredClusterType(ClusterType.REPLICA_SET)
//               }
               .build()
        )
    }

    @Bean
    fun transactionManager(reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory?): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory!!)
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