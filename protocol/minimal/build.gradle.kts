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
    arg("mavlink.packageName", "com.mmoczkowski.mavlink.minimal")
}

dependencies {
    implementation(project(":core"))
    ksp(project(":processor"))
}
