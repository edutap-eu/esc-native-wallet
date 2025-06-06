package eu.eduTap.core.push

import eu.eduTap.core.Card

interface PlatformSpecificPushHandler {
  suspend fun notifyAllCardHolders(card: Card)
}