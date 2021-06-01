import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version PluginVersions.springBoot apply false

    kotlin("jvm") version PluginVersions.kotlin
    kotlin("kapt") version PluginVersions.kotlin
    kotlin("plugin.serialization") version PluginVersions.kotlin
    kotlin("plugin.spring") version PluginVersions.kotlin apply false

    `maven-publish`
}

allprojects {
    group = "com.github.shouwn"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.springframework.boot")

        plugin("kotlin")
        plugin("kotlin-kapt")
        plugin("kotlin-spring")
        plugin("kotlinx-serialization")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11

    dependencies {
        implementation(Dependencies.koltin)
        implementation(Dependencies.kotlinReflect)
        implementation(Dependencies.kotlinSerialization)

        implementation(platform(Dependencies.springBootBom))

        kapt(platform(Dependencies.springBootBom))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xinline-classes", "-Xjvm-default=enable")
            jvmTarget = "11"
        }
    }
}