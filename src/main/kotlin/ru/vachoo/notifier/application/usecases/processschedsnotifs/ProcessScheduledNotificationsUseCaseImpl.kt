package ru.vachoo.notifier.application.usecases.processschedsnotifs

import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.usecases.processschedsnotifs.`in`.ProcessScheduledNotificationsUseCase
import ru.vachoo.notifier.application.usecases.processschedsnotifs.out.NotificationSenderPort
import ru.vachoo.notifier.domain.entities.ScheduledNotification
import ru.vachoo.notifier.domain.enums.NotificationStatus

@Component
class ProcessScheduledNotificationsUseCaseImpl(
  val scheduledNotificationDbPort: ScheduledNotificationDbPort,
  val notificationSenderPort: NotificationSenderPort,
) : ProcessScheduledNotificationsUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  companion object {
    private const val MAX_RETRY_COUNT = 5
    private val RETRY_DELAYS =
      listOf(
        Duration.ZERO,
        Duration.ofMinutes(1),
        Duration.ofMinutes(2),
        Duration.ofMinutes(4),
        Duration.ofMinutes(8),
      )
  }

  @Transactional
  override fun process() {
    log.info("Processing scheduled notifications")
    val notifications = scheduledNotificationDbPort.findPendingForProcessing(100)
    log.info("Found {} notifications to process", notifications.size)

    notifications.forEach { notification -> processNotification(notification) }

    log.info("Processed {} notifications", notifications.size)
  }

  private fun processNotification(notification: ScheduledNotification) {
    val userId = notification.userId ?: return
    val message = notification.message

    log.debug("Processing notification id={} for user={}", notification.id, userId)

    val success =
      try {
        notificationSenderPort.send(userId, message)
      } catch (e: Exception) {
        log.error("Failed to send notification id={}: {}", notification.id, e.message)
        false
      }

    val now = OffsetDateTime.now(ZoneOffset.UTC)

    if (success) {
      notification.status = NotificationStatus.SENT
      notification.sentAt = now
      notification.nextRetryAt = null
      log.info("Notification sent successfully id={}", notification.id)
    } else {
      notification.retryCount++
      notification.status = NotificationStatus.FAILED

      if (notification.retryCount >= MAX_RETRY_COUNT) {
        notification.status = NotificationStatus.EXHAUSTED
        notification.nextRetryAt = null
        log.warn(
          "Notification exhausted after {} retries id={}",
          notification.retryCount,
          notification.id,
        )
      } else {
        val delay = RETRY_DELAYS.getOrElse(notification.retryCount - 1) { Duration.ofMinutes(8) }
        notification.nextRetryAt = now.plusMinutes(delay.toMinutes())
        log.warn(
          "Notification failed, retry {} in {} minutes id={}",
          notification.retryCount,
          delay.toMinutes(),
          notification.id,
        )
      }
    }

    notification.updatedAt = now
    scheduledNotificationDbPort.save(notification)
  }
}
