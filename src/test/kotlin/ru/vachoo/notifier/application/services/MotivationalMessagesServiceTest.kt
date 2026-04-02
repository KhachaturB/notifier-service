package ru.vachoo.notifier.application.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.commonports.out.LlmPort

@ExtendWith(MockitoExtension::class)
class MotivationalMessagesServiceTest {

  @Mock private lateinit var llmPort: LlmPort

  private lateinit var service: MotivationalMessagesService

  @BeforeEach
  fun setUp() {
    service = MotivationalMessagesService(llmPort)
  }

  @Test
  fun shouldGenerateMessages_WhenCalled() {
    val expectedMessages =
      listOf("Не забудьте о целях! 🎯", "Шаг к цели уже близко! 💪", "Прогресс — это путь! 🚀")
    whenever(llmPort.generateMotivationalMessages(10)).thenReturn(expectedMessages)

    service.generateMessages()

    val message = service.getRandomMessage()
    assertTrue(expectedMessages.contains(message))
    verify(llmPort).generateMotivationalMessages(10)
  }

  @Test
  fun shouldReturnMessage_WhenMessagesGenerated() {
    val expectedMessages = listOf("Тестовое сообщение 🎯")
    whenever(llmPort.generateMotivationalMessages(10)).thenReturn(expectedMessages)

    service.generateMessages()
    val message = service.getRandomMessage()

    assertEquals("Тестовое сообщение 🎯", message)
  }

  @Test
  fun shouldGenerateMessages_WhenGetRandomMessageCalledWithEmptyCache() {
    val expectedMessages = listOf("Новое сообщение 🌟")
    whenever(llmPort.generateMotivationalMessages(10)).thenReturn(expectedMessages)

    val message = service.getRandomMessage()

    assertEquals("Новое сообщение 🌟", message)
  }

  @Test
  fun shouldUseDefaultMessages_WhenLlmReturnsEmptyList() {
    whenever(llmPort.generateMotivationalMessages(10)).thenReturn(emptyList())

    service.generateMessages()
    val message = service.getRandomMessage()

    assertTrue(message.isNotEmpty())
    assertTrue(message.length <= 40)
  }

  @Test
  fun shouldUseDefaultMessages_WhenLlmReturnsEmptyListAndCacheEmpty() {
    whenever(llmPort.generateMotivationalMessages(10)).thenReturn(emptyList())

    val message = service.getRandomMessage()

    assertTrue(message.isNotEmpty())
    assertTrue(message.length <= 40)
  }
}
