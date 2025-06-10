package eu.eduTap.core

import eu.eduTap.core.card.apple.AppleWalletConfig
import eu.eduTap.core.card.google.GoogleWalletConfig

/**
 * Configuration for the card handler.
 */
class WalletHandlerConfig(
  val appleWalletConfig: AppleWalletConfig? = null,
  val googleWalletConfig: GoogleWalletConfig? = null,
)