package eu.eduTap.core.router

import kotlinx.serialization.Serializable

@Serializable
data class Person(
  val fullName: String,
  val identifier: String,
  val identifierCode: KeyValue?,
  val organisationCount: Int,
)

@Serializable
data class KeyValue(
  val key: String,
  val value: String,
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
  val cardStatusType: KeyValue,
  val cardType: KeyValue,
  val displayName: String,
  val expiresAt: String,
  val hasOwnerAuthorization: Boolean,
  val issuedAt: String,
  val person: CardPerson,
) {
  @Serializable
  data class CardPerson(
    val fullName: String,
    val identifier: String,
  )
}

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
  UNKNOWN,
  PASSIVE,
  SMART_NO_CDZ,
  SMART_CDZ,
  SMART_MAY_SP,
  SMART_PASSIVE,
  SMART_PASSIVE_EMULATION,
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
