/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.card.google

class GoogleWalletConfig(
  /**
   * The issuer ID for the Google Wallet API.
   * This is typically the same as the issuer ID used in the Google Pay API.
   * Can be found on the Google Pay & Wallet Console.
   */
  val issuerId: String,
  val googleApiKey: String,

  /**
   * The [classIdSuffix] is appended to the issuer ID to form the full class ID.
   */
  val classIdSuffix: String,

  val webServiceConfig: WebServiceConfig,
) {
  class WebServiceConfig(
    /**
     * Base URL of the web service handling the google wallet related requests.
     */
    val webServiceUrl: String,

    val icon: ByteArray,

    val logo: ByteArray,
  ) {
    init {
      require(webServiceUrl.startsWith("https://")) {
        "Web service URL must start with 'https://'. Provided: $webServiceUrl"
      }
    }
  }
}