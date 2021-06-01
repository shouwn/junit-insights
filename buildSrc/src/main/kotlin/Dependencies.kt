@file:Suppress("unused", "SpellCheckingInspection")

import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {
    const val koltin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"
    const val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1"

    const val springBootStarter = "org.springframework.boot:spring-boot-starter"
    const val springBootConfigurationProcessor = "org.springframework.boot:spring-boot-configuration-processor"
    const val springBootWeb = "org.springframework.boot:spring-boot-starter-web"
    const val springBootBom = "org.springframework.boot:spring-boot-dependencies:${PluginVersions.springBoot}"
    const val springBootTest = "org.springframework.boot:spring-boot-starter-test"

    const val springWeb = "org.springframework:spring-web"
    const val springTest = "org.springframework:spring-test"

    const val junit = "org.junit.jupiter:junit-jupiter"
    const val junitApi = "org.junit.jupiter:junit-jupiter-api"
    const val junitPlatformLauncher = "org.junit.platform:junit-platform-launcher"
    const val assertJ = "org.assertj:assertj-core"
    const val mockk = "io.mockk:mockk:1.11.0"
}

fun DependencyHandler.api(dependencies: List<Any>) {
    dependencies.forEach {
        add("api", it)
    }
}

fun DependencyHandler.implementation(dependencies: List<Any>) {
    dependencies.forEach {
        add("implementation", it)
    }
}

fun DependencyHandler.compileOnly(dependencies: List<Any>) {
    dependencies.forEach {
        add("compileOnly", it)
    }
}

fun DependencyHandler.testImplementation(dependencies: List<Any>) {
    dependencies.forEach {
        add("testImplementation", it)
    }
}