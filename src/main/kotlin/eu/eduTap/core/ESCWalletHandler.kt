/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core

import eu.eduTap.core.card.apple.AppleWalletHandler
import eu.eduTap.core.card.google.GoogleWalletHandler
import eu.eduTap.core.push.apple.APNHandler
import eu.eduTap.core.storage.apple.ApplePassStorageHandler
import eu.eduTap.core.web.AppleWalletWebServiceHandler
import eu.eduTap.core.web.GoogleWalletWebServiceHandler
import eu.eduTap.core.web.PlatformSpecificWebHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Currently supports:
 * - Apple Wallet ([eu.eduTap.core.card.apple.AppleWalletConfig])
 * - Google Wallet ([eu.eduTap.core.card.google.GoogleWalletConfig])
 */
open class ESCWalletHandler(val config: WalletHandlerConfig) {
  private val appleWalletHandler: AppleWalletHandler by lazy {
    require(config.appleWalletConfig != null) { "Apple Wallet is not configured." }
    AppleWalletHandler(config.appleWalletConfig)
  }

  private val appleWalletPushHandler: APNHandler? by lazy {
    if (config.appleWalletConfig?.pushServiceConfig != null) {
      APNHandler(config.appleWalletConfig)
    } else null
  }

  private val googleWalletHandler: GoogleWalletHandler by lazy {
    require(config.googleWalletConfig != null) { "Google Wallet is not configured." }
    GoogleWalletHandler(config.googleWalletConfig)
  }

  open fun getAppleWalletPass(studentCard: EuStudentCard): ByteArray = appleWalletHandler.generateSignedPass(studentCard)

  open fun getAppleWalletPassResponse(studentCard: EuStudentCard): PlatformSpecificWebHandler.BasicHttpResponse {
    return appleWalletHandler.generateSignedPassHttpResponse(studentCard)
  }

  fun getAppleWalletWebServiceHandler(storageHandler: ApplePassStorageHandler): AppleWalletWebServiceHandler {
    require(config.appleWalletConfig != null) { "Apple Wallet is not configured." }
    require(config.appleWalletConfig.webServiceConfig != null) { "Apple Wallet web service is not configured." }

    return AppleWalletWebServiceHandler(appleWalletHandler, storageHandler)
  }

  fun getGoogleWalletWebServiceHandler(): GoogleWalletWebServiceHandler {
    require(config.googleWalletConfig != null) { "Google Wallet is not configured." }
    return GoogleWalletWebServiceHandler(googleWalletHandler)
  }

  open fun getGoogleWalletPassJwt(studentCard: EuStudentCard): String = googleWalletHandler.getSignedJWTPass(studentCard)

  open fun getGoogleWalletAddUrl(studentCard: EuStudentCard): String = googleWalletHandler.getAddToWalletUrl(studentCard)

  suspend fun notifyAllCardHolders(card: Card) {
    appleWalletPushHandler?.notifyAllCardHolders(card)
  }

  fun notifyAllCardHoldersBlocking(card: Card) = runBlocking(Dispatchers.IO) { notifyAllCardHolders(card) }
}