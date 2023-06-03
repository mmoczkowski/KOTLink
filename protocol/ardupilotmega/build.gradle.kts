plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

ksp {
    arg("mavlink.schemaLocation", "$projectDir/mavlink/")
    arg("mavlink.packageName", "com.mmoczkowski.mavlink.ardupilotmega")
}

kotlin {
    sourceSets {
        val main by getting {
            //kotlin.srcDir("src/test/kotlin")
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))

            }
        }
    }
}

dependencies {
    implementation(project(":core"))
    ksp(project(":processor"))
}

tasks.test {
    useJUnitPlatform()
}
