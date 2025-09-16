/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.web

abstract class PlatformSpecificWebHandler {
  open class BasicHttpResponse(
    val statusCode: Int,
    open val body: Any? = null,
    val headers: Map<String, String> = emptyMap(),
  )
}