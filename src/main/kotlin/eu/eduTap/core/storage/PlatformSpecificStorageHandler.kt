/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.storage

import eu.eduTap.core.EuStudentCard

interface PlatformSpecificStorageHandler {
  fun getStudentCard(escn: String): EuStudentCard?
}