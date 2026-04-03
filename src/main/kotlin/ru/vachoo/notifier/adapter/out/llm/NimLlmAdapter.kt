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
                    """
          .trimIndent()

      val response =
        chatClientBuilder.build().prompt().user(prompt).call().entity(MessagesResponse::class.java)

      val messages = response?.messages?.filter { it.length <= 40 } ?: emptyList()

      log.info("Generated {} messages from LLM", messages.size)
      messages
    } catch (e: Exception) {
      log.error("Failed to generate messages: {}", e.message)
      emptyList()
    }
  }

  data class MessagesResponse(val messages: List<String>)
}
