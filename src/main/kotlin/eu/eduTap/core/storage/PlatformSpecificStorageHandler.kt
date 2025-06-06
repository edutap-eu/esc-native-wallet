package eu.eduTap.core.storage

import eu.eduTap.core.EuStudentCard

interface PlatformSpecificStorageHandler {
  fun getStudentCard(escn: String): EuStudentCard?
}