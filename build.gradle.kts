plugins {
  kotlin("jvm") version "2.1.0"
  `maven-publish`
}

group = "eu.eduTap.core"
version = "0.1-beta"

repositories {
  mavenCentral()
}

java {
  withSourcesJar()
  withJavadocJar()
}

val ktor_version = "3.1.3"

dependencies {
  implementation(kotlin("stdlib"))
  implementation("com.auth0:java-jwt:4.4.0")

  // For network requests
  implementation("io.ktor:ktor-client-okhttp:$ktor_version")
  implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

  // Needed for Apple Wallet
  implementation("de.brendamour:jpasskit:0.4.2")

  // Needed for Google API
  implementation("com.google.firebase:firebase-admin:9.4.2")
  implementation("com.google.apis:google-api-services-walletobjects:v1-rev20250506-2.0.0")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(21)
}

publishing {
  publications {
    create<MavenPublication>("jitpack") {
      from(components["java"])
    }
  }
}