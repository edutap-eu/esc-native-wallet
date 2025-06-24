package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

/**
 * ESCRouter is a client for interacting with the European Student Card (ESC) Router API.
 *
 * It provides methods to manage persons and cards, including creating, updating, retrieving, and deleting records,
 * as well as fetching card QR codes. The client handles authentication and serialization for API requests and responses.
 *
 * In case of an API error, it throws an [ESCRouterApiException] with the status code and error message.
 *
 * Links to the API documentation:
 * - Router: https://router.europeanstudentcard.eu/esc-rest/swagger-ui/index.html
 * - Sandbox: https://sandbox.europeanstudentcard.eu/esc-rest/swagger-ui/index.html
 *
 * @param apiToken The API token used for Bearer authentication.
 * @param baseUrl The base URL of the ESC Router API (default: https://router.europeanstudentcard.eu/, sandbox: https://sandbox.europeanstudentcard.eu/).
 */
class ESCRouter(
  apiToken: String,
  baseUrl: String = "https://router.europeanstudentcard.eu/",
) {
  private val apiUrl = "${baseUrl}esc-rest/api/v2/"

  private val httpClient = HttpClient {
    install(Auth) {
      bearer {
        loadTokens {
          BearerTokens(accessToken = apiToken, refreshToken = null)
        }
      }
    }
    install(ContentNegotiation) {
      json()
    }
  }

  val persons = PersonsApi(httpClient, apiUrl)
  val cards = CardsApi(httpClient, apiUrl)
}

class ESCRouterApiException(val statusCode: HttpStatusCode, val apiError: ApiErrorMessage) :
  Exception("API Error: ${apiError.code} - ${apiError.message}")
