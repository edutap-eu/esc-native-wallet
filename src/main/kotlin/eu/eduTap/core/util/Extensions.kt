package eu.eduTap.core.util

internal fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V>?): Map<K, V> = pairs.filterNotNull().toMap()