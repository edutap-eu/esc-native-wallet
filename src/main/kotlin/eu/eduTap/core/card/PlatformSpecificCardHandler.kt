package eu.eduTap.core.card

import eu.eduTap.core.EuStudentCard
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.roundToInt

abstract class PlatformSpecificCardHandler {
  protected fun generateQRCode(studentCard: EuStudentCard): String = studentCard.escn // TODO @Z Implement QR code generation

  protected fun InputStream.toByteArrayInputStream() = ByteArrayInputStream(this.readBytes())

  protected fun String.toUrl(): URL = URI.create(this).toURL()
}