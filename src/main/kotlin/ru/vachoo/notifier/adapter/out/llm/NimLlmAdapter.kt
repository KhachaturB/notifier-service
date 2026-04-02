package ru.vachoo.notifier.adapter.out.llm

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.commonports.out.LlmPort

@Component
class NimLlmAdapter(private val chatClientBuilder: ChatClient.Builder) : LlmPort {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun generateMotivationalMessages(count: Int): List<String> {
    return try {
      log.info("Generating {} motivational messages using NIM API", count)

      val prompt =
        """
                    Сгенерируй $count коротких мотивационных сообщений на русском языке.
                    Требования:
                    - Каждое сообщение не более 40 символов
                    - Позитивные и вдохновляющие
                    - С эмодзи
                    - Для приложения достижения целей

                    Верни только список сообщений, каждое с новой строки, без нумерации.
                    """
          .trimIndent()

      val response = chatClientBuilder.build().prompt(prompt).call().content()

      parseMessages(response, count)
    } catch (e: Exception) {
      log.error("Failed to generate messages: {}", e.message)
      emptyList()
    }
  }

  private fun parseMessages(response: String?, count: Int): List<String> {
    if (response.isNullOrBlank()) return emptyList()

    val messages =
      response.lines().map { it.trim() }.filter { it.isNotBlank() && it.length <= 40 }.take(count)

    log.info("Parsed {} messages from LLM response", messages.size)
    return messages
  }
}
