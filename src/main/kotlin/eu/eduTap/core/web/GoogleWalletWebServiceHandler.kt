/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.web

import eu.eduTap.core.card.google.GoogleWalletHandler
import eu.eduTap.core.util.scalePng
import java.io.ByteArrayInputStream

class GoogleWalletWebServiceHandler(private val walletHandler: GoogleWalletHandler) : PlatformSpecificWebHandler() {
  fun getImage(name: String): BasicHttpResponse {
    val image = walletHandler.imageResources
      .find { it.name == name.removePrefix(walletHandler.config.webServiceConfig.webServiceUrl) }
      ?: return BasicHttpResponse(statusCode = 404)

    val scaledImage = scalePng(image.imageData, targetWidth = image.targetWidth, targetHeight = image.targetHeight)
    return BasicHttpResponse(
      statusCode = 200,
      body = scaledImage,
      headers = mapOf(
        "Content-Type" to "image/png",
        "Content-Disposition" to "inline; filename=\"${image.name}\"",
        "Content-Length" to scaledImage.available().toString()
      )
    )
  }
}