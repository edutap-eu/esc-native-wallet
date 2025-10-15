/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.router

import io.ktor.client.plugins.api.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

internal val ContextLogger = createClientPlugin("ContextLogger") {
  onResponse { response ->
    val logger = currentCoroutineContext()[LoggerContext.Key] ?: return@onResponse
    logger.logResponse(LoggerResponse(response))
  }
}

/**
 * Coroutine context element for request/response logging for [ESCRouter] requests.
 * Per request only one logger can be used.
 *
 * ```kotlin
 * val logger = object : LoggerContext() {
 *   override suspend fun logResponse(response: LoggerResponse) {
 *     println("${response.method} ${response.url} -> ${response.statusCode}")
 *   }
 * }
 *
 * withContext(logger) {
 *   escRouter.persons.create(...) // -> "POST https://router.europeanstudentcard.eu/... -> 200"
 * }
 * ```
 */
abstract class LoggerContext : AbstractCoroutineContextElement(Key) {
  abstract suspend fun logResponse(response: LoggerResponse)

  companion object Key : CoroutineContext.Key<LoggerContext>
}

/**
 * Wrapper around [HttpResponse] to provide access to common properties as basic types without needing to import Ktor.
 */
class LoggerResponse internal constructor(private val response: HttpResponse) {
  val statusCode: Int get() = response.status.value
  val method: String get() = response.request.method.value
  val url: String get() = response.request.url.toString()
  val headers: Map<String, List<String>> get() = response.headers.toMap()

  suspend fun responseBodyAsText(): String = response.bodyAsText()

  /** Only supports text and form data bodies. Other body types will return "Body omitted". */
  fun requestBodyAsText(): String? = when (val body = response.request.content.unwrap()) {
    is OutgoingContent.NoContent -> null
    is TextContent -> body.text
    is FormDataContent -> body.formData.formUrlEncode()
    is OutgoingContent.ContentWrapper -> throw IllegalStateException("Unwrapped ContentWrapper")
    else -> "Body omitted${body.contentLength?.let { " ($it bytes)" }.orEmpty()}"
  }

  private fun OutgoingContent.unwrap(): OutgoingContent =
    if (this is OutgoingContent.ContentWrapper) delegate().unwrap() else this
}