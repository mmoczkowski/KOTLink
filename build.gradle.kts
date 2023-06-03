plugins {
    kotlin("jvm") version "1.8.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}

//buildscript {
//    dependencies {
//        classpath(kotlin("gradle-plugin", version = "1.8.0"))
//    }
//}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}