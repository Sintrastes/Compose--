import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
    }
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

plugins {
    id("com.android.library")
    kotlin("multiplatform").version("1.9.0")
}

tasks.withType(KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

group = "io.github.sintrastes"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}

kotlin {
    android {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
            }
        }
        // withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {

            }
        }
        val jvmTest by getting
    }
}

android {
    compileSdk = 24
    namespace = "com.example.myapp"
}
