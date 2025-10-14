/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.router

import io.ktor.client.plugins.api.*
import io.ktor.client.statement.*
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

internal val ContextLogger = createClientPlugin("ContextLogger") {
  onResponse { response ->
    val logger = currentCoroutineContext()[LoggerContext.Key] ?: return@onResponse
    logger.logResponse(response)
  }
}

/**
 * Coroutine context element for request/response logging for [ESCRouter] requests.
 * Per request only one logger can be used.
 *
 * ```kotlin
 * val logger = object : LoggerContext() {
 *   override suspend fun logResponse(response: HttpResponse) {
 *     println("${response.request.method.value} ${response.request.url} -> ${response.status.value}")
 *   }
 * }
 *
 * withContext(logger) {
 *   escRouter.persons.create(...) // -> "POST https://router.europeanstudentcard.eu/... -> 200"
 * }
 * ```
 */
abstract class LoggerContext : AbstractCoroutineContextElement(Key) {
  abstract suspend fun logResponse(response: HttpResponse)

  companion object Key : CoroutineContext.Key<LoggerContext>
}