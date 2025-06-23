package eu.eduTap.core.card.apple

import eu.eduTap.core.storage.apple.ApplePassStorageHandler
import java.io.ByteArrayInputStream

class AppleWalletConfig(
  /**
   * The pass type identifier. This is a unique identifier for the pass type.
   * Must match the one used in the pass certificate.
   *
   * Example: pass.eu.eduTap.demo.esc
   */
  val passTypeIdentifier: String,

  /**
   * This is a unique identifier for the apple developer account team.
   */
  val teamIdentifier: String,

  val organizationName: String,

  /**
   * The pass certificate. This usually comes ina .p12 file.
   */
  val passCert: ByteArrayInputStream,

  /**
   * If the pass certificate is password-protected, set this to the password.
   * If the pass certificate is not password-protected, set this to an empty string.
   */
  val passCertPassword: String = "",

  /**
   * The WWDR certificate. This usually comes in a .cer file.
   */
  val wwdrCert: ByteArrayInputStream,

  val icon: ByteArray,

  val logo: ByteArray,

  val webServiceConfig: WebServiceConfig? = null,
  val pushServiceConfig: PushServiceConfig? = null,
) {
  /**
   * Configure passes to use a web service for updates.
   * More information can be found here: https://developer.apple.com/documentation/walletpasses/adding-a-web-service-to-update-passes
   */
  class WebServiceConfig(
    /**
     * Base URL of the web service handling the pass updates.
     */
    val webServiceUrl: String,

    val storageHandler: ApplePassStorageHandler,
  )

  /**
   * Configure server-to-server push functionality to update passes
   */
  class PushServiceConfig(

    val keyId: String,

    /**
     * Usually comes as a .p8 file
     * Example: AuthKey_<KEYID>.p8
     */
    val authKey: ByteArrayInputStream,
  )
}