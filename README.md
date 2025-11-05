# esc-native-wallet

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![License: AGPL-3.0](https://img.shields.io/badge/AGPL--3.0-red?style=for-the-badge)
[![](https://jitpack.io/v/edutap-eu/esc-native-wallet.svg)](https://jitpack.io/#edutap-eu/esc-native-wallet)

### Kotlin library for the **European Student Card (ESC)**

`esc-native-wallet` lets universities issue and manage digital European Student Cards for
Apple Wallet and Google Wallet, built on top of the official ESC Router.

Built for Kotlin/JVM (Ktor, Spring) — and fully interoperable with Java.

- **ESC Router** client (HTTP API) for issuing & verifying ESC data.
- **Wallet passes**: generate **Apple Wallet** and **Google Wallet** passes for student IDs.

---

## Why

Integrating with the ESC Router and generating secure wallet passes is complex and time-consuming.
This library provides ready-to-use, type-safe client, wallet pass builders, and signing utilities
so you can go from prototype to production without reinventing the plumbing.

> Focus on student experience — not crypto signatures, manifests, or REST quirks.

---

## Highlights

#### ESC Router client
  - Typed client for ESC-R endpoints: *issue*, *update*, *verify*
  - Token management, retries, and pluggable HTTP engines (Ktor)
  - Built-in helpers for common workflows

#### Wallet passes
  - **Apple Wallet**: JSON + assets pipeline, manifest hashing, PKCS#7 signing
  - **Google Wallet**: class/object builders, JWT creation, issuer flows
  - Escape hatches for low-level PassKit or REST integrations

#### Security & privacy
  - No telemetry. No external tracking.
  - Minimal default logging; easy to redact sensitive data.

#### Developer Experience
  - Kotlin-first, Java-friendly
  - Pure JVM artifact — no native dependencies or toolchains

---

## Compatibility

- **Kotlin**: 2.1+
- **JDK**: 17+
- **ESC Router**: targets public ESC-R HTTP API.
- **Apple Wallet**: requires Apple Developer account, Pass Type ID & certificate.
- **Google Wallet**: requires Wallet Issuer account & credentials.

> You are responsible for complying with Apple/Google terms and ESC program rules.

---

## Installation

Add JitPack repository to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.edutap-eu:esc-native-wallet:1.0.3")
}
```

## Quick Start

Use `ESCWalletHandler` with your Apple/Google Wallet credentials to generate wallet passes.

```kotlin
val esc = object : EuStudentCard { /*...*/ }

val walletHandler = ESCWalletHandler(
    config = WalletHandlerConfig(
      appleWalletConfig = AppleWalletConfig(/*...*/),
      googleWalletConfig = GoogleWalletConfig(/*...*/),
    )
)

val applePass = walletHandler.getAppleWalletPassResponse(esc)
```
---

## Local development

Compile the project and publish it to your local maven repository:

```bash
./gradlew publishToMavenLocal
```

then add the dependency:

```xml

<dependency>
    <groupId>eu.eduTap.core</groupId>
    <artifactId>esc-native-wallet</artifactId>
    <version>0.1-beta</version>
</dependency>
```
---

## Contributing

Contributions and feedback are welcome!
If you’re planning substantial changes, please open an issue first to discuss scope.

---
## License

This software is open-source under the AGPL-3.0 license (GNU Affero General Public License v3.0).  

If you modify this software and make it available to users over a network, you must make the complete, corresponding source code of your modified version available to those users.

Commercial licensing (to use without AGPL obligations) is available — contact **partner-support@studo.com**.

See [`LICENSE`](./LICENSE) for full text.

---

## Professional Support

This project is maintained by [Studo](https://studo.com).

If your institution wants to issue **European Student Cards** or integrate **Wallet passes**,
we can help you get up and running quickly.

Get in touch: partner-support@studo.com
