plugins {
    kotlin("jvm") version "1.3.72"
}

group = "com.github.patrick-mc"
version = "0.1-SNAPSHOT"

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
    implementation("com.github.noonmaru:kommand:0.1.9")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    create<Copy>("distJar") {
        from(jar)
        into("W:\\Servers\\1.15.2\\plugins")
    }
}