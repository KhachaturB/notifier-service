package ru.vachoo.notifier.application.services

import java.util.concurrent.CopyOnWriteArrayList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.vachoo.notifier.application.commonports.out.LlmPort

@Service
class MotivationalMessagesService(private val llmPort: LlmPort) {

  private val log = LoggerFactory.getLogger(javaClass)

  private val messages = CopyOnWriteArrayList<String>()

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

  fun getRandomMessage(): String {
    return if (messages.isNotEmpty()) {
      messages.random()
    } else {
      defaultMessages.random()
    }
  }

  fun generateMessages() {
    log.info("Generating motivational messages")
    val generated = llmPort.generateMotivationalMessages(10)
    messages.clear()
    messages.addAll(generated)
    log.info("Generated {} motivational messages", messages.size)
  }
}
