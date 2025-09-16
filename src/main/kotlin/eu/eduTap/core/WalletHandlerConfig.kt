/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core

import eu.eduTap.core.card.apple.AppleWalletConfig
import eu.eduTap.core.card.google.GoogleWalletConfig

/**
 * Configuration for the card handler.
 */
class WalletHandlerConfig(
  val appleWalletConfig: AppleWalletConfig? = null,
  val googleWalletConfig: GoogleWalletConfig? = null,
)