package ru.vachoo.notifier.adapter.out.notification

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.usecases.processschedsnotifs.out.NotificationSenderPort

@Component
class NotificationSender : NotificationSenderPort {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun send(userId: UUID, message: String): Boolean {
    log.info("Sending notification to user={}: {}", userId, message)
    log.warn(
      "STUB: This is a placeholder implementation. " +
        "Real implementation will send push notifications via Apple APNS."
    )
    return true
  }
}
