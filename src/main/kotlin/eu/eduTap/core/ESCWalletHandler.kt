package eu.eduTap.core

import eu.eduTap.core.card.apple.AppleWalletHandler
import eu.eduTap.core.push.apple.APNHandler
import eu.eduTap.core.storage.apple.ApplePassStorageHandler
import eu.eduTap.core.web.apple.AppleWalletWebServiceHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Currently supports:
 * - Apple Wallet ([eu.eduTap.core.card.apple.AppleWalletConfig])
 * - Google Wallet ([WalletHandlerConfig.GoogleWalletConfig])
 */
open class ESCWalletHandler(val config: WalletHandlerConfig) {
  private val appleWalletHandler by lazy {
    require(config.appleWalletConfig != null) { "Apple Wallet is not configured." }
    AppleWalletHandler(config.appleWalletConfig)
  }

  private val appleWalletPushHandler: APNHandler? by lazy {
    if (config.appleWalletConfig?.pushServiceConfig != null) {
      APNHandler(config.appleWalletConfig)
    } else null
  }

  open fun getAppleWalletPass(studentCard: EuStudentCard): ByteArray = appleWalletHandler.generateSignedPass(studentCard)

  fun getAppleWalletWebServiceHandler(storageHandler: ApplePassStorageHandler): AppleWalletWebServiceHandler {
    require(config.appleWalletConfig != null) { "Apple Wallet is not configured." }
    require(config.appleWalletConfig.webServiceConfig != null) { "Apple Wallet web service is not configured." }

    return AppleWalletWebServiceHandler(appleWalletHandler, storageHandler)
  }

  suspend fun notifyAllCardHolders(card: Card) {
    appleWalletPushHandler?.notifyAllCardHolders(card)
  }

  fun notifyAllCardHoldersBlocking(card: Card) = runBlocking(Dispatchers.IO) { notifyAllCardHolders(card) }
}