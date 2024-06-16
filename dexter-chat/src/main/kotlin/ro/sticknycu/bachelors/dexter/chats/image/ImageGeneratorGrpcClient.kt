package ro.sticknycu.bachelors.dexter.chats.image

import ImageGeneratorGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ImageGeneratorGrpcClient(
    @Value("\${grpc.host}")
    val grpcHost: String
) {
    private val channel = ManagedChannelBuilder.forAddress(grpcHost, 50051)
        .usePlaintext()
        .build()

    private val stub: ImageGeneratorGrpcKt.ImageGeneratorCoroutineStub = ImageGeneratorGrpcKt.ImageGeneratorCoroutineStub(channel)

    fun generateImage(imageData: ByteArray): Mono<Map<String, ByteArray>> {
        val request = ImageGeneratorOuterClass.ImageRequest.newBuilder()
            .setImageData(com.google.protobuf.ByteString.copyFrom(imageData))
            .build()

        return mono {
            val response = stub.generateImage(request)

            val cannyEdgeBase64 = response.cannyEdge.toByteArray()
            val generatedImageBase64 = response.generatedImage.toByteArray()

            mapOf(
                "canny_edge" to cannyEdgeBase64,
                "generated_image" to generatedImageBase64
            )
        }
    }
}