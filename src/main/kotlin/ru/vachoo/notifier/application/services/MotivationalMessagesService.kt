package ru.vachoo.notifier.application.services

import java.util.concurrent.CopyOnWriteArrayList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.vachoo.notifier.application.commonports.out.LlmPort

@Service
class MotivationalMessagesService(private val llmPort: LlmPort) {

  private val log = LoggerFactory.getLogger(javaClass)

  private val messages = CopyOnWriteArrayList<String>()

  fun getRandomMessage(): String {
    return if (messages.isNotEmpty()) {
      messages.random()
    } else {
      generateMessages()
      if (messages.isNotEmpty()) messages.random() else "Время двигаться к целям! 🚀"
    }
  }

  fun generateMessages() {
    log.info("Generating motivational messages")
    val generated = llmPort.generateMotivationalMessages(10)
    if (generated.isNotEmpty()) {
      messages.clear()
      messages.addAll(generated)
      log.info("Generated {} motivational messages", messages.size)
    }
  }
}
