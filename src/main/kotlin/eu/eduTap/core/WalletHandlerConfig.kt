package eu.eduTap.core

import eu.eduTap.core.card.apple.AppleWalletConfig

/**
 * Configuration for the card handler.
 */
class WalletHandlerConfig(
  val appleWalletConfig: AppleWalletConfig? = null,
  val googleWalletConfig: GoogleWalletConfig? = null,
) {

  /**
   * Configuration for Google Wallet.
   *
   * TODO @Z Think about adding separate implementations for Generic Pass type & Generic Private Pass type.
   */
  class GoogleWalletConfig(
    /**
     * The issuer ID. This is a unique identifier for the issuer.
     * Must match the one used in the google developer account.
     *
     * Example: 12345678901234567890
     *
     */
    val issuerId: String,

    /**
     * The service account key. This usually comes in a .json file.
     */
    val serviceAccountKey: String,
  )
}