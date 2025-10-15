plugins {
  kotlin("jvm") version "2.1.0"
  `maven-publish`
  kotlin("plugin.serialization") version "2.1.0"
  id("com.diffplug.spotless") version "7.0.0"
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

spotless {
  kotlin {
    target("**/*.kt")
    licenseHeader(
      """
      /* 
       * Copyright (c) 2025 Student & Campus Services GmbH
       * SPDX-License-Identifier: AGPL-3.0-or-later
       */
      """.trimIndent()
    )
  }
  java {
    target("**/*.java")
    licenseHeader(
      """
      /* 
       * Copyright (c) 2025 Student & Campus Services GmbH
       * SPDX-License-Identifier: AGPL-3.0-or-later
       */
      """.trimIndent()
    )
  }
}

val ktor_version = "3.1.3"
val kotlinx_serialization_version: String = "1.8.0" // https://github.com/Kotlin/kotlinx.serialization/releases

dependencies {
  implementation(kotlin("stdlib"))
  implementation("com.auth0:java-jwt:4.4.0")

  // For network requests
  implementation("io.ktor:ktor-client-okhttp:$ktor_version")
  implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
  implementation("io.ktor:ktor-client-auth:${ktor_version}")
  implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")

  // Needed for Apple Wallet
  implementation("de.brendamour:jpasskit:0.4.2")

  // Needed for Google API
  implementation("com.google.firebase:firebase-admin:9.4.2")
  implementation("com.google.apis:google-api-services-walletobjects:v1-rev20250506-2.0.0")

  testImplementation(kotlin("test"))
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
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
  repositories {
    mavenLocal()
  }
}