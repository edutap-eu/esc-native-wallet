package eu.eduTap.core.util

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.roundToInt

/**
 * Scale a PNG *down* while keeping its aspect ratio.
 *
 * * Provide **width**, **height**, or **both** as *upper bounds*.
 * * When both are given, the image is shrunk until **both** sides fit.
 * * Upscaling is disabled by default; pass `allowUpscale = true` if you really
 *   want to enlarge smaller images.
 *
 * The caller‑supplied [originalStream] is consumed. Use a
 * `ByteArrayInputStream` if you need to reuse the bytes afterwards.
 */
internal fun scalePng(
  originalStream: ByteArrayInputStream,
  targetWidth: Int? = null,
  targetHeight: Int? = null,
  allowUpscale: Boolean = false,
): ByteArrayInputStream {
  require(targetWidth != null || targetHeight != null) { "Specify at least one of targetWidth or targetHeight." }

  /*───────────────────────────────────────────────────────────────────────*/
  /* 1 ▏Load source + dimensions                                          */
  /*───────────────────────────────────────────────────────────────────────*/
  val original: BufferedImage = ImageIO.read(originalStream)
  val origW = original.width
  val origH = original.height

  /*───────────────────────────────────────────────────────────────────────*/
  /* 2 ▏Figure out scale factor                                           */
  /*───────────────────────────────────────────────────────────────────────*/
  val scale = when {
    targetWidth != null && targetHeight != null -> {
      // Scale to fit both dimensions --> take the smaller scale
      minOf(targetWidth.toDouble() / origW, targetHeight.toDouble() / origH)
    }

    targetWidth != null -> targetWidth.toDouble() / origW
    targetHeight != null -> targetHeight.toDouble() / origH

    else -> throw IllegalStateException("This should never happen.")
  }

  // Never enlarge unless explicitly requested
  val effectiveScale = if (!allowUpscale && scale > 1.0) 1.0 else scale

  val destW = (origW * effectiveScale).roundToInt().coerceAtLeast(1)
  val destH = (origH * effectiveScale).roundToInt().coerceAtLeast(1)

  /*───────────────────────────────────────────────────────────────────────*/
  /* 3 ▏Draw the resized image                                            */
  /*───────────────────────────────────────────────────────────────────────*/
  val resized = BufferedImage(destW, destH, BufferedImage.TYPE_INT_ARGB)
  resized.createGraphics().apply {
    setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    drawImage(original, 0, 0, destW, destH, null)
    dispose()
  }

  /*───────────────────────────────────────────────────────────────────────*/
  /* 4 ▏Encode back to PNG + return                                       */
  /*───────────────────────────────────────────────────────────────────────*/
  return ByteArrayOutputStream()
    .apply { ImageIO.write(resized, "png", this) }
    .toByteArray()
    .let { ByteArrayInputStream(it) }
}