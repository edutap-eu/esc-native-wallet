package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class PersonsApi(httpClient: HttpClient, apiUrl: String) : ESCApi(httpClient, apiUrl) {
  private val personApiUrl = "${apiUrl}persons"

  suspend fun get(esi: String): Person {
    return makeRequest { httpClient.get("${personApiUrl}/${esi}") }
  }

  suspend fun create(
    fullName: String,
    esi: String,
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
    fullName: String,
    esi: String,
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
