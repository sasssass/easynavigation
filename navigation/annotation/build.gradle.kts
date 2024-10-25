plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["java"])

                groupId = "com.github.sasssass"
                artifactId = "compose-navigation-annotation"
                version = "0.2"
            }
        }
    }
}