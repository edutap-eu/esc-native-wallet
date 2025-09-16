/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.router

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

abstract class ESCApi(protected val httpClient: HttpClient, protected val apiUrl: String) {
  /**
   * Wrapper for making API requests and handling responses. Throws an [ESCRouterApiException] if the response status code is not as
   * expected.
   */
  protected suspend inline fun <reified T> makeRequest(request: () -> HttpResponse): T {
    val response = request()

    if (!response.status.isSuccess()) {
      val errorMessage = response.body<ApiErrorMessage>()
      throw ESCRouterApiException(response.status, errorMessage)
    }

    return response.body()
  }
}

class ESCRouterApiException(val statusCode: HttpStatusCode, val apiError: ApiErrorMessage) :
  Exception("API Error: ${apiError.code} - ${apiError.message}")
