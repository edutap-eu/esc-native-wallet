/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.push

import eu.eduTap.core.Card

interface PlatformSpecificPushHandler {
  suspend fun notifyAllCardHolders(card: Card)
}