package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class VerifyApi(httpClient: HttpClient, apiUrl: String) : ESCApi(httpClient, apiUrl) {
  private val cardApiUrl = "${apiUrl}cards/verify"

  /**
   * Verify a card using its ESCN (European Student Card Number).
   *
   * @param escn The ESCN of the card to verify.
   * @return A [CardVerificationDetails] object containing the verification details of the card.
   * @throws ESCRouterApiException with code `ER-0001` in case no card with the given [escn] exists.
   */
  suspend fun escn(escn: String): CardVerificationDetails {

    val verifiedCard = makeRequest<CardVerificationDetails> {
      httpClient.get(cardApiUrl) {
        url {
          appendPathSegments(escn)
        }
      }
    }

    return verifiedCard
  }
}