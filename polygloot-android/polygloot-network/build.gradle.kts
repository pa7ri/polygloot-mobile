import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources
import java.util.Properties

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.polygloot.mobile.polygloot.network"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 31

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "OPENAI_API_KEY", "\"${properties.getProperty("OPENAI_API_KEY")}\"")
    }

    kotlin {
        jvmToolchain(17)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.logging)
                implementation(libs.logback.classic)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(libs.hilt.android)

                configurations.getByName("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "com.google.dagger",
                        "hilt-android-compiler",
                        libs.versions.hilt.get()
                    )
                )
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        all {
            languageSettings.apply {
                languageVersion = "1.9"
                apiVersion = "1.9"
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                progressiveMode = true
            }

        }
    }
    // Configure all compilations of all targets:
    targets.all {
        compilations.all {
            compilerOptions.configure {
                allWarningsAsErrors.set(true)
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}
dependencies {
    implementation(libs.lottie.compose)
}
