package eu.eduTap.core.util

import eu.eduTap.core.util.LocalizedStringConfig.SupportedLanguage.*
import kotlinx.serialization.Serializable

// English, German
@Serializable
class LocalizedString(
  val en: String,
  val de: String? = null,
) {
  constructor() : this(en = "", de = null)
  constructor(universalString: String) : this(en = universalString)

  fun get(language: String?): String {
    return when (language) {
      "de" -> de ?: en
      else -> en
    }
  }

  fun get(language: LocalizedStringConfig.SupportedLanguage?): String {
    return when (language) {
      De -> de ?: en
      else -> en
    }
  }

  // When concatenating localized strings, make sure to fall back to "en" localization,
  // because null + " test" -> "null test". When falling back, the worst-case is that random english sentences appear in a german text.
  infix operator fun plus(other: LocalizedString) = LocalizedString(
    en = this.get(En) + other.get(En),
    de = this.get(De) + other.get(De),
  )

  infix operator fun plus(other: String) = LocalizedString(
    en = this.get(En) + other,
    de = this.get(De) + other,
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as LocalizedString

    if (en != other.en) return false
    if (de != other.de) return false

    return true
  }

  override fun hashCode(): Int {
    var result = en.hashCode()
    result = 31 * result + (de?.hashCode() ?: 0)
    return result
  }

  fun toMap(): Map<String, String> {
    return mapOfNotNull(
      "en" to en,
      de?.let { "de" to it },
    )
  }

  companion object {
    fun from(map: Map<String, String>): LocalizedString {
      return LocalizedString(
        en = map["en"] ?: "",
        de = map["de"],
      )
    }
  }
}

object LocalizedStringConfig {
  enum class SupportedLanguage(val langCode: String, val languageName: String) {
    En("en", "English"),
    De("de", "Deutsch");

    companion object {
      fun fromLangCode(lang: String): SupportedLanguage? = entries.find { it.langCode == lang }
    }
  }
}