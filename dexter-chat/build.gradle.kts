import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("com.google.protobuf") version "0.9.4"
}

group = "ro.sticknycu.bachelors"
version = "0.0.1-SNAPSHOT"

buildscript {
	dependencies {
		classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.13")
	}
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
	// scoate
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:XXX:osx-x86_64")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // build.gradle.kts
    implementation ("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:0.5.2")
    implementation ("org.jetbrains.kotlinx:kotlin-deeplearning-onnx:0.5.2")
    implementation ("org.jetbrains.kotlinx:kotlin-deeplearning-visualization:0.5.2")
    // https://mvnrepository.com/artifact/org.openpnp/opencv
    implementation("org.openpnp:opencv:4.9.0-0")

    // https://mvnrepository.com/artifact/io.grpc/protoc-gen-grpc-kotlin
	// https://mvnrepository.com/artifact/io.grpc/grpc-protobuf
	implementation("io.grpc:grpc-protobuf:1.64.0")

	implementation("io.grpc:grpc-stub:1.64.0")
	implementation("io.grpc:grpc-netty:1.64.0")
    api("com.google.protobuf:protobuf-java-util:3.13.0")
	implementation("io.grpc:grpc-all:1.64.0")
	api("io.grpc:grpc-kotlin-stub:1.4.1")
	// https://mvnrepository.com/artifact/io.grpc/protoc-gen-grpc-kotlin
	implementation("io.grpc:protoc-gen-grpc-kotlin:1.4.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
	implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.13")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

//    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
//    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
	protoc{
		artifact = "com.google.protobuf:protoc:3.25.1"
	}
	generatedFilesBaseDir = "$projectDir/src/main/proto/generated"
	plugins {
		id("grpc"){
//			artifact = "io.grpc:protoc-gen-grpc-java:1.64.0" // :osx-x86_64
			artifact = "io.grpc:protoc-gen-grpc-java:1.64.0:osx-x86_64"
		}
		id("grpckt") {
			artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				id("grpc")
				id("grpckt")
			}
		}
	}
}