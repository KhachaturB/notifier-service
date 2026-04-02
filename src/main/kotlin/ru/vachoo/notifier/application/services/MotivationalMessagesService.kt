package ru.vachoo.notifier.application.services

import java.util.concurrent.CopyOnWriteArrayList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class MotivationalMessagesService(
    @Value("\${nim.api.url:}") private val nimApiUrl: String,
    @Value("\${nim.api.key:}") private val nimApiKey: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val messages = CopyOnWriteArrayList<String>()

    private val restClient = RestClient.create()

    private val defaultMessages =
        listOf(
            "Не забудьте о своих целях на сегодня! 🎯",
            "Время сделать шаг к своей цели! 💪",
            "Прогресс — это путь, а не пункт назначения! 🚀",
            "Вы уже сегодня работали над своими целями? ⏰",
            "Маленькие шаги ведут к большим результатам! 🌟",
        )

    fun getRandomMessage(): String {
        return if (messages.isNotEmpty()) {
            messages.random()
        } else {
            defaultMessages.random()
        }
    }

    fun generateMessages() {
        if (nimApiUrl.isBlank() || nimApiKey.isBlank()) {
            log.warn("NIM API URL or key not configured, using default messages")
            messages.clear()
            messages.addAll(defaultMessages)
            return
        }

        try {
            log.info("Generating motivational messages using NIM API")

            val prompt =
                """
                Сгенерируй 10 коротких мотивационных сообщений на русском языке для пользователей приложения для достижения целей.
                Сообщения должны быть:
                - Короткими (не более 100 символов)
                - Позитивными и вдохновляющими
                - С эмодзи
                - Разнообразными по содержанию

                Верни только список сообщений, каждое с новой строки, без нумерации.
                """.trimIndent()

            val requestBody =
                mapOf(
                    "model" to "meta/llama-3.1-8b-instruct",
                    "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
                    "max_tokens" to 1000,
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

            parseAndStoreMessages(response)
            log.info("Generated {} motivational messages", messages.size)
        } catch (e: Exception) {
            log.error("Failed to generate motivational messages: {}", e.message)
            if (messages.isEmpty()) {
                messages.addAll(defaultMessages)
            }
        }
    }

    private fun parseAndStoreMessages(response: String?) {
        if (response.isNullOrBlank()) return

        try {
            val content =
                response
                    .substringAfter("\"content\":\"")
                    .substringBefore("\"")
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")

            val newMessages =
                content
                    .lines()
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .take(10)

            if (newMessages.isNotEmpty()) {
                messages.clear()
                messages.addAll(newMessages)
            }
        } catch (e: Exception) {
            log.warn("Failed to parse NIM response: {}", e.message)
        }
    }
}
