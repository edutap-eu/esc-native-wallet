package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

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
      json(Json {
        ignoreUnknownKeys = true
      })
    }
  }

  val persons = PersonsApi(httpClient, apiUrl)
  val cards = CardsApi(httpClient, apiUrl)
}

// TODO Remove
suspend fun main() {
  val escRouter = ESCRouter(apiToken = "AT-OFV7hySg91SQ6t5GfKIM47qrj0JybT60", baseUrl = "https://sandbox.europeanstudentcard.eu/")

  val person = escRouter.persons.get(esi = "urn:schac:personalUniqueCode:int:esi:example.org:test")
  val card = escRouter.cards.get(esi = "urn:schac:personalUniqueCode:int:esi:example.org:test")

  println(person)
  println(card)

  var newPerson = escRouter.persons.get(esi = "urn:schac:personalUniqueCode:int:esi:example.org:test1")

  if (newPerson == null) {
    newPerson = escRouter.persons.create(
      esi = "urn:schac:personalUniqueCode:int:esi:example.org:test1",
      fullName = "Test Code",
      organisationIdentifier = "999489456",
    )
  }

  println(newPerson)

  val updatedPerson = escRouter.persons.update(
    esi = "urn:schac:personalUniqueCode:int:esi:example.org:test1",
    fullName = "Test Code",
    organisationIdentifier = "999489456",
    academicLevel = AcademicLevel.BACHELOR,
    email = "test1@example.org",
  )

  println(updatedPerson)

  escRouter.persons.delete(esi = "urn:schac:personalUniqueCode:int:esi:example.org:test1")

  newPerson = escRouter.persons.get(esi = "urn:schac:personalUniqueCode:int:esi:example.org:test1")

  println(newPerson) // Should be null after deletion
}
