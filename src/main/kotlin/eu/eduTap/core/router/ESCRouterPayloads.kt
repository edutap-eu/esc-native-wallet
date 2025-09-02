package eu.eduTap.core.router

import kotlinx.serialization.Serializable

@Serializable
data class Person(
  val fullName: String,
  val identifier: String,
  val identifierCode: KeyLabel?,
  val organisationCount: Int,
)

@Serializable
data class KeyLabel(
  val key: String,
  val label: String,
)

@Serializable
data class PersonUpdate(
  val fullName: String,
  val identifier: String,
  val identifierCode: String?,
  val personOrganisationUpdateViews: List<PersonOrganisationUpdate>,
) {
  @Serializable
  data class PersonOrganisationUpdate(
    val academicLevel: AcademicLevel?,
    val email: String?,
    val fax: String?,
    val organisationIdentifier: String,
    val phone: String?,
  )
}

enum class AcademicLevel {
  BACHELOR,
  MASTER,
  DOCTORATE,
}

@Serializable
data class Card(
  val cardNumber: String,
  val cardStatusType: TypedKeyLabel<CardStatusType>,
  val cardType: TypedKeyLabel<CardType>,
  val displayName: String,
  val expiresAt: String,
  val hasOwnerAuthorization: Boolean,
  val issuedAt: String,
  val person: CardPerson,
  val issuer: Issuer,
) {
  @Serializable
  data class TypedKeyLabel<T>(
    val key: T,
    val label: String,
  )

  @Serializable
  data class Issuer(
    val fullLabel: String,
    val id: Int,
    val identifier: String,
    val name: String,
    val organisationType: KeyLabel,
    val schacHomeOrganization: String,
    val status: KeyLabel,
    val website: String,
  )
}

@Serializable
data class CardLite(
  val cardNumber: String,
  val displayName: String,
  val expiresAt: String,
  val hasOwnerAuthorization: Boolean,
  val issuedAt: String,
  val person: CardPerson,
)

@Serializable
data class CardVerificationDetails(
  val cardNumber: String,
  val cardType: Card.TypedKeyLabel<CardType>,
  val cardStatusType: Card.TypedKeyLabel<CardStatusType>,
  val expiresAt: String,
  val issuer: Issuer,
) {
  @Serializable
  data class Issuer(
    val identifier: String,
    val name: String,
  )
}

@Serializable
data class CardPerson(
  val fullName: String,
  val identifier: String,
)

@Serializable
data class CardUpdate(
  val cardNumber: String?,
  val cardStatusType: CardStatusType,
  val cardType: CardType,
  val displayName: String,
  val expiresAt: String,
  val issuedAt: String,
  val issuerIdentifier: String,
  val personIdentifier: String,
  val processorIdentifier: String?,
)

enum class CardStatusType {
  ACTIVE,
  INACTIVE,
  EXPIRED,
}

enum class CardType {
  UNKNOWN, // Unknown / None
  PASSIVE, // Physical passive card, with no electronic component
  SMART_NO_CDZ, // Physical smart card, without ESC data zone
  SMART_CDZ, // Physical smart card, with ESC data zone
  SMART_MAY_SP, // Physical smart card, with custom data by service providers
  SMART_PASSIVE, // Digital passive card, with no electronic component
  SMART_PASSIVE_EMULATION, // Digital smart card, with physical card emulation
}

enum class QrCodeOrientation {
  HORIZONTAL,
  VERTICAL,
}

enum class QrCodeColor {
  NORMAL,
  INVERTED,
}

enum class QrCodeSize {
  XS,
  S,
  M
}

@Serializable
data class ApiErrorMessage(
  val code: String,
  val message: String,
)
