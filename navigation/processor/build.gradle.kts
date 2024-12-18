plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.ksp)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17" // Set this to match your Java target
    }
}

dependencies {
    implementation(project(":navigation:annotation"))
    implementation(libs.symbol.processing)
    implementation(libs.kotlinpoet)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["java"])

                groupId = "com.github.sasssass"
                artifactId = "compose-navigation-annotation-helper"
                version = "0.2"
            }
        }
    }
}