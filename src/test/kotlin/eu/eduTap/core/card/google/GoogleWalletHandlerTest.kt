/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.card.google

import com.auth0.jwt.JWT
import eu.eduTap.core.EuStudentCard
import java.security.KeyPairGenerator
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class GoogleWalletHandlerTest {
  private val googleWalletHandler = run {
    // Prepare a synthetic service account configuration and student card test data
    val clientEmail = "test-service@demo-project.iam.gserviceaccount.com"
    val serviceAccountJson = buildServiceAccountJson(clientEmail)
    GoogleWalletHandler(
      GoogleWalletConfig(
        issuerId = "issuer123",
        googleApiKey = serviceAccountJson,
        classIdSuffix = "escClass",
        webServiceConfig = GoogleWalletConfig.WebServiceConfig(
          webServiceUrl = "https://example.com",
          icon = byteArrayOf(0x01),
          logo = byteArrayOf(0x02),
        )
      )
    )
  }

  @Test
  fun getSignedJWTPass_containsExpectedClaims() {
    // Generate a signed Google Wallet JWT using the handler under test
    val jwt = googleWalletHandler.getSignedJWTPass(TestStudentCard())

    // Decode and assert the relevant JWT claims
    val decoded = JWT.decode(jwt)
    assertEquals("savetowallet", decoded.getClaim("typ").asString())
    assertEquals(listOf("google"), decoded.audience)

    val origins = decoded.getClaim("origins").asList(String::class.java)
    assertEquals(listOf("example.com"), origins)

    val payload = decoded.getClaim("payload").asMap() ?: fail("payload claim missing")
    val genericPrivatePasses = payload["genericPrivatePasses"] as? List<*> ?: fail("genericPrivatePasses list missing")
    val firstPass = genericPrivatePasses.firstOrNull() as? Map<*, *> ?: fail("genericPrivatePasses entry missing")
    assertEquals("GENERIC_PRIVATE_PASS_TYPE_UNSPECIFIED", firstPass["type"])
  }

  private fun buildServiceAccountJson(clientEmail: String): String {
    val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    val privateKey = keyPair.private
    val privateKeyPem = buildString {
      append("-----BEGIN PRIVATE KEY-----\n")
      Base64.getEncoder().encodeToString(privateKey.encoded).chunked(64).forEach {
        append(it)
        append("\n")
      }
      append("-----END PRIVATE KEY-----\n")
    }

    return """
      {
        "type": "service_account",
        "project_id": "demo-project",
        "private_key_id": "test-key-id",
        "private_key": "${privateKeyPem.replace("\n", "\\n")}",
        "client_email": "$clientEmail",
        "client_id": "123456789012345678901",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/${clientEmail.replace("@", "%40")}"
      }
    """.trimIndent()
  }

  private data class TestStudentCard(
    override val escn: String = "escn-1234567890",
    override val expiresAt: String = "2040-12-31",
    override val issuedAt: String = "2025-01-01",
    override val issuerHEIName: String = "Demo University",
    override val fullName: String = "Ada Lovelace",
    override val esi: String = "urn:schac:personalUniqueCode:int:esi:AT:1234567890",
    override val dateOfBirth: String? = null,
    override val additionalFields: Map<String, String> = mapOf("Program" to "Computer Science"),
    override val heroImage: ByteArray? = null,
  ) : EuStudentCard
}
