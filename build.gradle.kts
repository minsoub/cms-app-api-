import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("jacoco")

    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("kapt") version "1.6.21"
}

group = "com.bithumbsystems"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-configuration-processor:2.6.7")

    // Kotlin
    kapt("org.springframework.boot:spring-boot-configuration-processor:2.6.7")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("io.arrow-kt:arrow-core:1.0.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.0.1")

    // open api
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.9")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.9")

    // netty M1
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.77.Final:osx-aarch_64")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson-spring-boot-starter:3.17.6")
    implementation("it.ozimov:embedded-redis:0.7.1")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("com.google.code.gson:gson:2.9.0")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.amshove.kluent:kluent:1.68")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("io.projectreactor:reactor-test:3.4.18")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.2")
    testImplementation("io.mockk:mockk:1.12.4")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    mainClass.set("com.bithumbsystems.cms.CmsAppApiApplication")
}

tasks.jar {
    enabled = false
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Test>("test") {
    systemProperty("spring.profiles.active", "test")
}
