package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class VerifyApi(httpClient: HttpClient, apiUrl: String) : ESCApi(httpClient, apiUrl) {
  private val cardApiUrl = "${apiUrl}cards/verify"

  suspend fun escn(escn: String): CardVerified {

    val verifiedCard = makeRequest<CardVerified> {
      httpClient.get(cardApiUrl) {
        url {
          appendPathSegments(escn)
        }
      }
    }

    return verifiedCard
  }
}