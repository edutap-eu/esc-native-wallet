package eu.eduTap.core.router

import kotlinx.serialization.Serializable

@Serializable
data class Person(
  val fullName: String,
  val identifier: String,
  val identifierCode: KeyLabel?,
  val organisationCount: Int,
) {
  @Serializable
  data class KeyLabel(
    val key: String,
    val label: String,
  )
}


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
  val cardStatusType: KeyLabel<CardStatusType>,
  val cardType: KeyLabel<CardType>,
  val displayName: String,
  val expiresAt: String,
  val hasOwnerAuthorization: Boolean,
  val issuedAt: String,
  val person: CardPerson,
) {
  @Serializable
  data class KeyLabel<T>(
    val key: T,
    val label: String,
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
