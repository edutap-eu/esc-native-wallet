package eu.eduTap.core.web

abstract class PlatformSpecificWebHandler {
  open class BasicHttpResponse(
    val statusCode: Int,
    open val body: Any? = null,
    val headers: Map<String, String> = emptyMap(),
  )
}