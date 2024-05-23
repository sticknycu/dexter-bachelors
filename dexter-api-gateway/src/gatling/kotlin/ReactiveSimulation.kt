import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.gatling.core.config.GatlingConfiguration
import io.gatling.http.Predef
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.http.HttpDsl.status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

class ReactiveSimulation : Simulation() {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ReactiveSimulation::class.java)
    }


    private val httpProtocol: HttpProtocolBuilder = Predef.http(GatlingConfiguration.loadForTest())
        .baseUrl("http://localhost:3333")

    private val imageGenerationBlockingScenario: ScenarioBuilder = CoreDsl.scenario("Image Generation - Reactive Scenario")
        .exec(
            HttpDsl.http("Image Generation - Reactive")
                .get("/reactive/test")
                .check(status().saveAs("status"))
                .header("Content-Type", "application/json")
//                .body(CoreDsl.StringBody(Templates.template))
        )

    internal object Templates {
        val template = Function<Session, String> { session ->
            val objectMapper =
                ObjectMapper()

//            val registerRequest: RegisterRequest = RegisterRequest.builder()
//                .firstname(generateRandomString())
//                .lastname(generateRandomString())
//                .email(generateRandomString())
//                .password(generateRandomString())
//                .role(Arrays.stream(Role.values()).findAny().get())
//                .gender(Gender.MALE)
//                .address(generateRandomString())
//                .mobileNumber(generateRandomString())
//                .build()

            try {
                return@Function objectMapper.writeValueAsString("")
            } catch (e: JsonProcessingException) {
                logger.atError()
                    .log("An error has occurred while unmarshall object to json.")
            }.toString()
        }
    }

    init {
        setUp(
            imageGenerationBlockingScenario.injectOpen(
                OpenInjectionStep.atOnceUsers(5_000),
                CoreDsl.rampUsers(25_000).during(10),
                CoreDsl.constantUsersPerSec(5_000.0).during(10).randomized()
            ).protocols(httpProtocol::protocol)
        )
    }
}