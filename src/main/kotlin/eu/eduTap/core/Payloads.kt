package eu.eduTap.core

interface Card {
  val appleWalletPassSerialNumber: String
  val googleWalletObjectId: String
}

/**
 * This interface represents the payload of a European Student Card (ESC).
 */
interface EuStudentCard : Card {
  /**
   * ESCN stands for Euorpean Student Card Number.
   * It is a unique identifier for the student card.
   */
  val escn: String
  override val appleWalletPassSerialNumber: String get() = escn
  override val googleWalletObjectId: String get() = escn

  val expiresAt: String // Example: 2040-12-31
  val issuedAt: String // Example: 2040-12-31

  /**
   * The issuer HEI (Higher Education Institution) is the institution that issued the card.
   */
  val issuerHEIName: String


  val fullName: String

  /**
   * The ESI (European Student Identifier) is a unique identifier for the student. It should be unique on a European level.
   *
   * Example: urn:schac:personalUniqueCode:int:esi:AT:xxxxxxxxxx
   */
  val esi: String

  val dateOfBirth: String?

  /**
   * This map is put on "the back side" of the card.
   */
  val additionalFields: Map<String, String>

  /**
   * The hero image is the main image displayed on the card.
   * It is usually a PNG image, a headshot of the student.
   * It is recommended to use a square image with a size of 512x512 pixels. If the image is not square, it will be cropped to fit.
   * If it's null, no image will be displayed.
   */
  val heroImage: ByteArray?
}
