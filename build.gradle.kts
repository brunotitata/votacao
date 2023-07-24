import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.avast.gradle.docker-compose") version "0.14.9"
	kotlin("jvm") version "1.7.0"
	kotlin("kapt") version "1.7.0"
	kotlin("plugin.spring") version "1.7.0"
	kotlin("plugin.jpa") version "1.7.0"
}

group = "br.com.votacao"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

val mockkVersion = "1.12.0"
val valiktorVersion = "0.12.0"
val kotestVersion = "4.6.3"
val logstashEncoderVersion = "7.2"
val kotlinLogging = "2.1.23"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.flywaydb:flyway-core")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.cloud:spring-cloud-stream:3.2.9")
	runtimeOnly("org.springframework.cloud:spring-cloud-stream-binder-rabbit:3.2.9")

	implementation("org.valiktor:valiktor-core:$valiktorVersion")
	testImplementation("org.valiktor:valiktor-test:$valiktorVersion")
	implementation("org.valiktor:valiktor-javatime:$valiktorVersion")
	implementation("org.valiktor:valiktor-spring-boot-starter:$valiktorVersion")

	implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")
	implementation("io.github.microutils:kotlin-logging:$kotlinLogging")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:$mockkVersion")
	testImplementation("com.ninja-squad:springmockk:3.1.1")
	testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.0.1")
	testImplementation("io.projectreactor:reactor-test:3.4.11")

	implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
