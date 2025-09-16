/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.push.apple

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import eu.eduTap.core.Card
import eu.eduTap.core.card.apple.AppleWalletConfig
import eu.eduTap.core.push.PlatformSpecificPushHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.utils.io.core.Closeable
import okhttp3.Protocol
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date
import java.time.Instant

class APNHandler(val config: AppleWalletConfig) : PlatformSpecificPushHandler, Closeable {
  private val pushServiceConfig = config.pushServiceConfig
    ?: throw IllegalArgumentException("APNHandler requires a pushServiceConfig to be set in the AppleWalletConfig")

  private val webServiceConfig = config.webServiceConfig
    ?: throw IllegalArgumentException("APNHandler requires a webServiceConfig to be set in the AppleWalletConfig")

  private val privateKey = run {
    val content = String(pushServiceConfig.authKey.readBytes())
      .replace("-----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "")
      .replace("\\s".toRegex(), "")
    val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(content))

    KeyFactory.getInstance("EC").generatePrivate(keySpec) as ECPrivateKey
  }

  private lateinit var authorizationJwt: String
  private var authorizationJwtGenerationDate = Date(0)
  private fun regenerateAuthorizationJwt() {
    val now = Date()

    authorizationJwt = JWT.create()
      .withKeyId(pushServiceConfig.keyId)
      .withIssuer(config.teamIdentifier)
      .withIssuedAt(now)
      .withExpiresAt(Date.from(Instant.now().plusSeconds(3_600)))
      .sign(Algorithm.ECDSA256(null, privateKey))
    authorizationJwtGenerationDate = now
  }

  private val client = HttpClient(OkHttp) {
    expectSuccess = false // we'll handle errors ourselves
    engine {
      config { protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1)) }
    }
  }

  suspend fun push(deviceToken: String, retryCount: Int = 0) {
    val host = "https://api.push.apple.com"

    // regenerate the JWT if it is older than 30 minutes --> always ensure that the JWT is valid for the request
    if (authorizationJwtGenerationDate.time + 1_800_000 < Date().time) regenerateAuthorizationJwt()

    val response = client.post("$host/3/device/$deviceToken") {
      method = HttpMethod.Post
      contentType(ContentType.Application.Json)
      setBody("{}") // empty JSON payload for passes

      header("apns-topic", config.passTypeIdentifier) // passTypeId is always the APNS topic
      header("apns-push-type", "pass")
      header("Authorization", "bearer $authorizationJwt")
    }

    when (response.status.value) {
      400 -> {
        // BadDeviceToken --> Token doesn’t belong to your topic or you mixed prod/sandbox TODO delete registered device
      }

      403 -> {
        if (retryCount > 3) throw IllegalStateException("Too many retries for APN push")

        // ExpiredProviderToken --> JWT expired
        regenerateAuthorizationJwt()
        push(deviceToken, retryCount = retryCount + 1) // retry
      }

      410 -> {
        // Unregistered --> Device dropped the pass → delete its registration
        webServiceConfig.storageHandler.unregisterPassToDevice(
          passTypeIdentifier = config.passTypeIdentifier,
          deviceLibraryId = deviceToken,
        )
      }

      200 -> Unit // Success

      else -> throw IllegalStateException("Unexpected response code: ${response.status.value}")
    }
  }

  override fun close() = client.close()

  override suspend fun notifyAllCardHolders(card: Card) {
    val storageHandler = webServiceConfig.storageHandler

    val devices = storageHandler.getRegisteredDevicesForPass(
      passTypeIdentifier = config.passTypeIdentifier,
      serialNumber = card.appleWalletPassSerialNumber,
    )

    devices.forEach { device -> push(deviceToken = device.pushToken) }
  }
}