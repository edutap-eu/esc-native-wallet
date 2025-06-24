package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/***
 * ESI Format:
 *      - For countries with a nation wide scope student ID PREFIX:CN:STUDENTCODE
 *      - For countries with a HEI wide scope student ID PREFIX:SchacHomeOrganization:STUDENTCODE
 *    CN - Country code of the institution (ISO 3166-1 norm)
 *    PIC - Participation Identification Code of the insitution where the student is enrolled
 *    PREFIX - urn:schac:personalUniqueCode:int:esi
 *    SchacHomeOrganization is the domain of the Higher Education Institution
 *    STUDENTCODE - Student unique code in the HEI where he is enrolled. If the country have national/regional identity code, this code will be national/regional wide, otherwise it will be the home institution own identifier.
 */
class PersonsApi(httpClient: HttpClient, apiUrl: String) : ESCApi(httpClient, apiUrl) {
  private val personApiUrl = "${apiUrl}persons"

  suspend fun get(esi: String): Person {
    return makeRequest { httpClient.get("${personApiUrl}/${esi}") }
  }

  suspend fun create(
    esi: String,
    fullName: String,
    organisationIdentifier: String,
    academicLevel: AcademicLevel? = null,
    email: String? = null,
    phone: String? = null,
    fax: String? = null,
  ): Person {
    return makeRequest {
      httpClient.post(personApiUrl) {
        contentType(ContentType.Application.Json)

        setBody(
          PersonUpdate(
            fullName = fullName,
            identifier = esi,
            identifierCode = "ESI",
            personOrganisationUpdateViews = listOf(
              PersonUpdate.PersonOrganisationUpdate(
                academicLevel = academicLevel,
                email = email,
                fax = fax,
                organisationIdentifier = organisationIdentifier,
                phone = phone,
              )
            )
          )
        )
      }
    }
  }

  suspend fun update(
    esi: String,
    fullName: String,
    organisationIdentifier: String,
    academicLevel: AcademicLevel? = null,
    email: String? = null,
    phone: String? = null,
    fax: String? = null,
  ): Person {
    return makeRequest {
      httpClient.put("${personApiUrl}/${esi}") {
        contentType(ContentType.Application.Json)

        setBody(
          PersonUpdate(
            fullName = fullName,
            identifier = esi,
            identifierCode = "ESI",
            personOrganisationUpdateViews = listOf(
              PersonUpdate.PersonOrganisationUpdate(
                academicLevel = academicLevel,
                email = email,
                fax = fax,
                organisationIdentifier = organisationIdentifier,
                phone = phone,
              )
            )
          )
        )
      }.body()
    }
  }

  suspend fun delete(esi: String) {
    makeRequest<String> {
      httpClient.delete("${personApiUrl}/${esi}")
    }
  }
}
