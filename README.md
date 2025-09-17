# esc-native-wallet

Kotlin library for the **European Student Card (ESC)** stack:
- **ESC Router** client (HTTP API) for issuing & verifying ESC data.
- **Wallet passes**: generate **Apple Wallet** and **Google Wallet** passes for student IDs.

Works with Kotlin/JVM projects (Ktor, Spring) and Java apps via standard interop.

---

## Why

Integrating with the **ESC Router** and pushing student IDs into **Apple/Google Wallet** is fiddly and easy to get wrong. `esc-native-wallet` provides a type-safe client, pass builders, and signing helpers so you can ship fast without reinventing the plumbing.

---

## Highlights

- **ESC Router client**
    - Typed client for ESC-R REST endpoints (issue, update, verify).
    - Token handling, retries, and pluggable HTTP engine (Ktor).

- **Wallet passes**
    - **Apple Wallet**: pass JSON & assets pipeline, manifest hashing, signing.
    - **Google Wallet**: class/object builders, JWT creation, issuer flows.
    - Escape hatches to drop to native PassKit/REST when needed.

- **Security & privacy**
    - No telemetry; no phone-home.
    - Minimal default logging; hooks for redaction.

- **DX**
    - Kotlin-first; Java-friendly.
    - Pure JVM artifact; no native toolchains.

---

## Compatibility

- **Kotlin**: 2.1+
- **JDK**: 17+
- **ESC Router**: targets public ESC-R HTTP API.
- **Apple Wallet**: requires Apple Developer account, Pass Type ID & certificate.
- **Google Wallet**: requires Wallet Issuer account & credentials.

> You are responsible for complying with Apple/Google terms and ESC program rules.

---

## Status

**Stable
**: [![](https://jitpack.io/v/studoverse/esc-native-wallet.svg)](https://jitpack.io/#studoverse/esc-native-wallet)

---

## Contributing

PRs welcome. For substantial changes, please open an issue to align on scope.

### Local development with maven

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

## License

**GNU Affero General Public License v3.0**.  
If you modify and run this software over a network, you must make the complete corresponding source of your modified version available to users of that network service.

Commercial licensing (to use without AGPL obligations) is available — contact **office@studo.com**.

See [`LICENSE`](./LICENSE) for full text.

