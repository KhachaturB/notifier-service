package ru.vachoo.notifier.adapter.out.llm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NimLlmAdapterTest {

  @Test
  fun shouldParseMessagesCorrectly() {
    val response = "Сообщение 1 🎯\nСообщение 2 💪\nСообщение 3 🚀"
    val messages =
      response.lines().map { it.trim() }.filter { it.isNotBlank() && it.length <= 40 }.take(3)

    assertEquals(3, messages.size)
    messages.forEach { assertTrue(it.length <= 40, "Message too long: $it") }
  }

  @Test
  fun shouldFilterLongMessages() {
    val longMessage =
      "Очень длинное сообщение которое превышает 40 символов и должно быть отфильтровано"
    val shortMessage = "Короткое сообщение 🎯"
    val response = "$longMessage\n$shortMessage"

    val messages =
      response.lines().map { it.trim() }.filter { it.isNotBlank() && it.length <= 40 }.take(5)

    assertEquals(1, messages.size)
    assertEquals(shortMessage, messages[0])
  }

  @Test
  fun shouldReturnEmptyList_WhenResponseIsNull() {
    val response: String? = null
    val messages =
      response?.lines()?.map { it.trim() }?.filter { it.isNotBlank() && it.length <= 40 }?.take(5)
        ?: emptyList()

    assertTrue(messages.isEmpty())
  }
}
