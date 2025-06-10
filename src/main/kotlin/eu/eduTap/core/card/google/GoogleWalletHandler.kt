package eu.eduTap.core.card.google

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.api.services.walletobjects.model.TextModuleData
import com.google.api.services.walletobjects.model.TranslatedString
import com.google.auth.oauth2.ServiceAccountCredentials
import eu.eduTap.core.EuStudentCard
import eu.eduTap.core.card.PlatformSpecificCardHandler
import eu.eduTap.core.util.LocalizedString
import eu.eduTap.core.web.GoogleWalletWebServiceHandler
import java.io.ByteArrayInputStream
import java.security.interfaces.RSAPrivateKey
import java.text.SimpleDateFormat
import java.util.Date

class GoogleWalletHandler(val config: GoogleWalletConfig) : PlatformSpecificCardHandler() {
  private val fullClassId = "${config.issuerId}.${config.classIdSuffix}"
  private val googleCredentials = ServiceAccountCredentials.fromStream(config.googleApiKey.byteInputStream())

  fun getAddToWalletUrl(card: EuStudentCard): String = "https://pay.google.com/gp/v/save/${getSignedJWTPass(card)}"

  @Suppress("SimpleDateFormat")
  fun getSignedJWTPass(card: EuStudentCard): String = JWT.create()
    .withClaim("typ", "savetowallet")
    .withIssuer(googleCredentials.clientEmail)
    .withAudience("google")
    .withClaim("origins", listOf(config.webServiceConfig.webServiceUrl.toUrl().host))
    .withClaim("payload", mapOf("genericPrivatePasses" to listOf(createPassObjectJson(card))))
    .withIssuedAt(Date())
    .withExpiresAt(SimpleDateFormat("yyyy-MM-dd").parse(card.expiresAt))
    .sign(Algorithm.RSA256(null, googleCredentials.privateKey as RSAPrivateKey))

  private fun createPassObjectJson(card: EuStudentCard) = mapOf(
    "id" to "$fullClassId.${card.googleWalletObjectId}",
    "type" to "GENERIC_PRIVATE_PASS_TYPE_UNSPECIFIED",
    "header" to LocalizedString(en = "European Student Card", de = "Europäische Studentenausweis").toTranslatedString(),
    "hexBackgroundColor" to "#1A438F",

    "title" to LocalizedString(universalString = card.fullName).toTranslatedString(),
    "titleLabel" to LocalizedString(en = "Name", de = "Name").toTranslatedString(),

    "textModulesData" to listOf(
      createTextModule(
        id = "row2left",
        header = LocalizedString(en = "University", de = "Hochschule").toTranslatedString(),
        body = card.issuerHEIName
      ),
      when {
        card.dateOfBirth != null -> createTextModule(
          id = "row2right",
          header = LocalizedString(en = "Date of birth", de = "Geburtsdatum").toTranslatedString(),
          body = card.dateOfBirth!!
        )

        else -> createTextModule(
          id = "row2right",
          header = LocalizedString(en = "ESI number").toTranslatedString(),
          body = card.esi
        )
      },
      createTextModule(
        id = "row3right",
        header = LocalizedString(en = "Valid until").toTranslatedString(),
        body = card.expiresAt
      ),
    ).plus(card.additionalFields.map { (label, value) ->
      createTextModule(
        id = label.lowercase(),
        header = LocalizedString(universalString = label).toTranslatedString(),
        body = value
      )
    }),
    "barcode" to mapOf(
      "type" to "QR_CODE", "value" to generateQRCode(card),
    ),
    "headerLogo" to mapOf(
      "sourceUri" to mapOf("uri" to logo.fullUrl),
      "contentDescription" to LocalizedString("Logo of EU").toTranslatedString()
    )
  )

  private fun LocalizedString.toTranslatedString(extraLanguages: Map<String, String> = emptyMap()): com.google.api.services.walletobjects.model.LocalizedString {
    return com.google.api.services.walletobjects.model.LocalizedString().apply {
      defaultValue = TranslatedString().apply { language = "en-US"; value = this@toTranslatedString.en }
      translatedValues = listOf(
        TranslatedString().apply { language = "en-US"; value = this@toTranslatedString.en },
        TranslatedString().apply { language = "de-DE"; value = this@toTranslatedString.get("de") },
      ) + extraLanguages.map { (language, translation) ->
        TranslatedString().apply { this.language = language; this.value = translation }
      }
    }
  }

  private fun createTextModule(
    id: String,
    header: com.google.api.services.walletobjects.model.LocalizedString,
    body: String,
  ): TextModuleData = TextModuleData().apply {
    this.id = id
    this.localizedHeader = header
    this.body = body
  }

  inner class ImageResource(val name: String, val imageData: ByteArrayInputStream, val targetWidth: Int, val targetHeight: Int) {
    val fullUrl get() = config.webServiceConfig.webServiceUrl.removeSuffix("/") + "/" + name
  }

  private val icon = ImageResource(
    name = "icon.png",
    imageData = config.webServiceConfig.icon,
    targetWidth = 90,
    targetHeight = 90,
  )

  private val logo = ImageResource(
    name = "logo.png",
    imageData = config.webServiceConfig.logo,
    targetWidth = 90,
    targetHeight = 90,
  )

  internal val imageResources = listOf(icon, logo)
}