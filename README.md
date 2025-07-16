# esc-native-wallet

### Local development with maven

Compile the porject and publish it to your local maven repository:

```bash
./gradlew publishToMavenLoca
```

then add the dependency:

```xml

<dependency>
  <groupId>eu.eduTap.core</groupId>
  <artifactId>esc-native-wallet</artifactId>
  <version>0.1-beta</version>
</dependency>
```