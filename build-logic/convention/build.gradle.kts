import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}


dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        register("kotlink.jvm") {
            id = "kotlink.jvm"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("kotlink.maven") {
            id = "kotlink.maven"
            implementationClass = "MavenConventionPlugin"
        }
        register("kotlink.signing") {
            id = "kotlink.signing"
            implementationClass = "SigningConventionPlugin"
        }
    }
}
