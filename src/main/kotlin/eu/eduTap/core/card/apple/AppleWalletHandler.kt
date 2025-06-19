package eu.eduTap.core.card.apple

import de.brendamour.jpasskit.PKBarcode
import de.brendamour.jpasskit.PKField
import de.brendamour.jpasskit.PKPass
import de.brendamour.jpasskit.enums.PKBarcodeFormat
import de.brendamour.jpasskit.enums.PKPassType
import de.brendamour.jpasskit.passes.PKGenericPass
import de.brendamour.jpasskit.signing.PKFileBasedSigningUtil
import de.brendamour.jpasskit.signing.PKPassTemplateInMemory
import de.brendamour.jpasskit.signing.PKSigningInformationUtil
import eu.eduTap.core.Card
import eu.eduTap.core.EuStudentCard
import eu.eduTap.core.card.PlatformSpecificCardHandler
import eu.eduTap.core.util.scalePng
import eu.eduTap.core.web.PlatformSpecificWebHandler
import java.awt.Color
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.security.SecureRandom

class AppleWalletHandler(val config: AppleWalletConfig) : PlatformSpecificCardHandler() {
  private inner class PreScaledImage(val baseImage: ByteArrayInputStream, val nativeWidthBound: Int, val nativeHeightBound: Int) {
    private fun scaleToFactor(factor: Int) = scalePng(
      originalStream = baseImage, targetWidth = nativeWidthBound * factor, targetHeight = nativeHeightBound * factor
    )

    val sdImage = scaleToFactor(factor = 1) // *.png
    val retinaImage = scaleToFactor(factor = 2) // *@2x.png
    val retinaHdImage = scaleToFactor(factor = 3) // *@3x.png
  }

  private val signingInfo = PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
    config.passCert,
    config.passCertPassword,
    config.wwdrCert
  ) ?: throw IllegalArgumentException("Failed to load signing information from passCert and wwdrCert.")


  private val icon = PreScaledImage(config.icon.toByteArrayInputStream(), nativeWidthBound = 29, nativeHeightBound = 29)
  private val logo = PreScaledImage(config.logo.toByteArrayInputStream(), nativeWidthBound = 160, nativeHeightBound = 50)

  fun generateSignedPass(studentCard: EuStudentCard): ByteArray {
    val pass = buildPass(studentCard)
    val template = buildTemplate(studentCard.heroImage)

    return PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass, template, signingInfo)
  }

  fun generateSignedPassHttpResponse(studentCard: EuStudentCard): PlatformSpecificWebHandler.BasicHttpResponse {
    val pass = generateSignedPass(studentCard)
    return PlatformSpecificWebHandler.BasicHttpResponse(
      statusCode = 200,
      body = pass,
      headers = mapOf(
        "Content-Type" to "application/vnd.apple.pkpass",
        "Content-Disposition" to "attachment; filename=\"${studentCard.escn}.pkpass\"",
        "Content-Length" to pass.size.toString()
      )
    )
  }

  // TODO make pass not shareable
  private fun buildPass(studentCard: EuStudentCard): PKPass {
    fun pkField(key: String, label: String? = null, value: String) = PKField.builder().apply {
      key(key)
      label?.let { label(it) }
      value(value)
    }.build()

    return PKPass.builder().apply {
      pass(
        PKGenericPass.builder()
          .passType(PKPassType.PKGenericPass)
          .headerField(pkField(key = "header", value = "European Student Card"))
          .primaryField(pkField("fullName", "Name", studentCard.fullName))
          .secondaryFields(
            listOf(
              pkField("university", "University", studentCard.issuerHEIName),
            )
          )
          .auxiliaryFields(
            listOf(
              pkField("expiryDate", "Valid until", studentCard.expiresAt),
              when {
                studentCard.dateOfBirth != null -> pkField("dateOfBirth", "Date of birth", studentCard.dateOfBirth!!)
                else -> pkField("esi", "ESI number", studentCard.esi)
              }
            )
          )
          .backFields(
            listOfNotNull(
              pkField("escn", "ESCN", studentCard.escn),
              pkField("esi", "ESI", studentCard.esi),
            ).plus(studentCard.additionalFields.map { pkField(it.key.lowercase(), it.key, it.value) })
          )
      )
      barcodeBuilder(
        PKBarcode.builder()
          .format(PKBarcodeFormat.PKBarcodeFormatQR)
          .message(generateQRCode(studentCard))
          .messageEncoding(Charset.forName("utf-8"))
      )

      formatVersion(1)
      passTypeIdentifier(config.passTypeIdentifier)
      serialNumber(studentCard.appleWalletPassSerialNumber)
      teamIdentifier(config.teamIdentifier)
      organizationName(config.organizationName)

      description("European Student Card")
      backgroundColor(Color(26, 67, 143))
      foregroundColor(Color.WHITE)

      if (config.webServiceConfig != null) {
        webServiceURL(config.webServiceConfig.webServiceUrl.toUrl())
        // Note: We are not re-using authentication tokens here, just not rotating them every time the pass gets fetched
        authenticationToken(studentCard.getAuthenticationToken() ?: generateNewAuthenticationToken())
      }
    }.build()
  }

  private fun buildTemplate(heroImage: ByteArrayInputStream?) = PKPassTemplateInMemory().apply {
    addFile(PKPassTemplateInMemory.PK_ICON, icon.sdImage)
    addFile(PKPassTemplateInMemory.PK_ICON_RETINA, icon.retinaImage)
    addFile(PKPassTemplateInMemory.PK_ICON_RETINAHD, icon.retinaHdImage)

    addFile(PKPassTemplateInMemory.PK_LOGO, logo.sdImage)
    addFile(PKPassTemplateInMemory.PK_LOGO_RETINA, logo.retinaImage)
    addFile(PKPassTemplateInMemory.PK_LOGO_RETINAHD, logo.retinaHdImage)

    if (heroImage != null) {
      val thumbnail = PreScaledImage(heroImage, nativeWidthBound = 90, nativeHeightBound = 90)
      addFile(PKPassTemplateInMemory.PK_THUMBNAIL, thumbnail.sdImage)
      addFile(PKPassTemplateInMemory.PK_THUMBNAIL_RETINA, thumbnail.retinaImage)
      addFile(PKPassTemplateInMemory.PK_THUMBNAIL_RETINAHD, thumbnail.retinaHdImage)
    }
  }

  /**
   * Gets the [eu.eduTap.core.storage.apple.ApplePassStorageHandler.RegisteredPass.authenticationToken] for a specific [Card].
   * Returns null if the [Card] was not registered yet.
   */
  private fun Card.getAuthenticationToken(): String? {
    require(config.webServiceConfig != null) { "Apple Wallet web service is not configured." }

    val pass = config.webServiceConfig.storageHandler.getRegisteredPass(
      passTypeIdentifier = config.passTypeIdentifier,
      serialNumber = appleWalletPassSerialNumber
    ) ?: return null

    return pass.authenticationToken
  }

  companion object {
    private val secureRandom = SecureRandom()

    private fun generateNewAuthenticationToken(): String {
      return ByteArray(32)
        .apply { secureRandom.nextBytes(this) } // 256 bits of entropy
        .joinToString("") { "%02x".format(it) }
    }
  }
}