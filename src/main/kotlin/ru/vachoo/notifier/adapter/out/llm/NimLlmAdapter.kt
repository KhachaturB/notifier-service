package ru.vachoo.notifier.adapter.out.llm

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import ru.vachoo.notifier.application.commonports.out.LlmPort

@Component
class NimLlmAdapter(
  @Value("\${nim.api.url:}") private val nimApiUrl: String,
  @Value("\${nim.api.key:}") private val nimApiKey: String,
) : LlmPort {

  private val log = LoggerFactory.getLogger(javaClass)

  private val restClient = RestClient.create()

  private val defaultMessages =
    listOf(
      "Не забудьте о целях! 🎯",
      "Шаг к цели уже близко! 💪",
      "Прогресс — это путь! 🚀",
      "Время для целей! ⏰",
      "Маленькие шаги! 🌟",
      "Вы можете всё! 💫",
      "Движение вперёд! 🌈",
      "Цели ждут вас! 🎊",
      "Успех близко! 🔥",
      "Верьте в себя! 💖",
    )

  override fun generateMotivationalMessages(count: Int): List<String> {
    if (nimApiUrl.isBlank() || nimApiKey.isBlank()) {
      log.warn("NIM API not configured, returning default messages")
      return defaultMessages.take(count)
    }

    try {
      log.info("Generating {} motivational messages using NIM API", count)

      val prompt =
        """
                Сгенерируй $count коротких мотивационных сообщений на русском языке.
                Требования:
                - Каждое сообщение не более 40 символов
                - Позитивные и вдохновляющие
                - С эмодзи
                - Для приложения достижения целей

                Верни JSON массив строк. Пример: ["Сообщение 1", "Сообщение 2"]
                """
          .trimIndent()

      val requestBody =
        mapOf(
          "model" to "meta/llama-3.1-8b-instruct",
          "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
          "max_tokens" to 500,
          "temperature" to 0.8,
        )

      val response =
        restClient
          .post()
          .uri(nimApiUrl)
          .header("Authorization", "Bearer $nimApiKey")
          .contentType(MediaType.APPLICATION_JSON)
          .body(requestBody)
          .retrieve()
          .body(String::class.java)

      val messages = parseMessages(response)
      return if (messages.isNotEmpty()) {
        messages.take(count)
      } else {
        defaultMessages.take(count)
      }
    } catch (e: Exception) {
      log.error("Failed to generate messages: {}", e.message)
      return defaultMessages.take(count)
    }
  }

  private fun parseMessages(response: String?): List<String> {
    if (response.isNullOrBlank()) return emptyList()

    return try {
      val content =
        response
          .substringAfter("\"content\":\"")
          .substringBefore("\"")
          .replace("\\n", "\n")
          .replace("\\\"", "\"")
          .replace("\\[", "[")
          .replace("\\]", "]")

      val arrayStart = content.indexOf('[')
      val arrayEnd = content.lastIndexOf(']')
      if (arrayStart >= 0 && arrayEnd > arrayStart) {
        content
          .substring(arrayStart, arrayEnd + 1)
          .removeSurrounding("[", "]")
          .split("\",")
          .map { it.trim().removeSurrounding("\"").removeSurrounding("\"", "\"").trim() }
          .filter { it.isNotBlank() && it.length <= 40 }
      } else {
        content.lines().map { it.trim() }.filter { it.isNotBlank() && it.length <= 40 }
      }
    } catch (e: Exception) {
      log.warn("Failed to parse NIM response: {}", e.message)
      emptyList()
    }
  }
}
