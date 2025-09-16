/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.util

internal fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V>?): Map<K, V> = pairs.filterNotNull().toMap()