/* 
 * Copyright (c) 2025 Student & Campus Services GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package eu.eduTap.core.router

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNull


class ESCRouterTest {

  private val apiToken = System.getenv("ESC_ROUTER_API_TOKEN")?.takeIf { it.isNotBlank() }
    ?: error("ESC_ROUTER_API_TOKEN environment variable not set")
  private val testOrganisationIdentifier = System.getenv("ESC_TEST_ORGANISATION_IDENTIFIER")?.takeIf { it.isNotBlank() }
    ?: error("ESC_TEST_ORGANISATION_IDENTIFIER environment variable not set")
  private val testProcessorIdentifier = System.getenv("ESC_TEST_PROCESSOR_IDENTIFIER")?.takeIf { it.isNotBlank() }
    ?: testOrganisationIdentifier

  private val escRouter = ESCRouter(apiToken, "https://sandbox.europeanstudentcard.eu/")

  private val logger = object : LoggerContext() {
    override suspend fun logResponse(response: LoggerResponse) {
      println("${response.method} ${response.url} -> ${response.statusCode}")
      println("  Request: ${response.requestBodyAsText().orEmpty()}")
      println("  Response: ${response.responseBodyAsText()}")
    }
  }

  private fun randomESI(): String {
    return "urn:schac:personalUniqueCode:int:esi:eu:${Random.nextInt(1_000_000, 9_999_999)}"
  }

  @Test
  fun testCreateFlow() = runTest(logger) {
    val esi = randomESI()

    val cards = escRouter.cards.getByEsi(esi = esi)
    assertEquals(cards.size, 0, "No cards should exist for random ESI")

    var person = escRouter.persons.get(esi)
    assertNull(person, "No person should exist for random ESI")

    person = escRouter.persons.create(
      esi = esi,
      fullName = "Test Person",
      organisationIdentifier = testOrganisationIdentifier,
      email = null
    )
    assertEquals(person.identifier, esi)
    assertEquals(person.organisations.size, 1)
    assertEquals(person.organisations.first().organisation.identifier, testOrganisationIdentifier)
    assertEquals(person.organisations.first().fullName, "Test Person")

    val card = escRouter.cards.create(
      esi = esi,
      displayName = "Test Person",
      expiresAt = LocalDate.now().plusDays(90),
      issuerIdentifier = testOrganisationIdentifier,
      processorIdentifier = testProcessorIdentifier,
      cardType = CardType.SMART_PASSIVE_EMULATION,
    )
    assertEquals(card.person.identifier, esi)
    assertEquals(card.issuer.identifier, testOrganisationIdentifier)
    assertEquals(card.processor.identifier, testProcessorIdentifier)
    assertEquals(card.displayName, "Test Person")
    assertEquals(card.cardStatusType.key, CardStatusType.ACTIVE)
    assertEquals(card.cardType.key, CardType.SMART_PASSIVE_EMULATION)

    escRouter.cards.delete(card.cardNumber)
    escRouter.persons.delete(esi)
  }
}