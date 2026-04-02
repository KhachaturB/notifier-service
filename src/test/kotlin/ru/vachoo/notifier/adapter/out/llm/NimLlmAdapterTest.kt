package ru.vachoo.notifier.adapter.out.llm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class NimLlmAdapterTest {

  @Test
  fun shouldReturnDefaultMessages_WhenApiUrlNotConfigured() {
    val adapter = NimLlmAdapter("", "")

    val messages = adapter.generateMotivationalMessages(5)

    assertEquals(5, messages.size)
    messages.forEach { assertTrue(it.length <= 40, "Message too long: $it") }
  }

  @Test
  fun shouldReturnCorrectCount_WhenRequested() {
    val adapter = NimLlmAdapter("", "")

    val messages = adapter.generateMotivationalMessages(3)

    assertEquals(3, messages.size)
  }

  @Test
  fun shouldReturnMessagesWithEmoji() {
    val adapter = NimLlmAdapter("", "")

    val messages = adapter.generateMotivationalMessages(10)

    assertTrue(messages.any { it.contains(Regex("[\\p{So}]")) })
  }

  @Test
  fun shouldReturnMessagesWithMax40Chars() {
    val adapter = NimLlmAdapter("", "")

    val messages = adapter.generateMotivationalMessages(10)

    messages.forEach { msg ->
      assertTrue(msg.length <= 40, "Message exceeds 40 chars: '${msg}' (${msg.length} chars)")
    }
  }
}
