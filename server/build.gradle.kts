plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinx.serialization)
    application
}

group = "ph.mart.shopper"
version = "1.0.0"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(libs.logback)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}