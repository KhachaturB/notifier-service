package ru.vachoo.notifier.application.usecases.createschedsnotifs

import java.time.Duration
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.notificationpreferences.NotificationPreferencesDbService
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.usecases.createschedsnotifs.`in`.CreateScheduledNotificationsUseCase
import ru.vachoo.notifier.domain.entities.ScheduledNotification
import ru.vachoo.notifier.domain.enums.NotificationStatus

@Component
class CreateScheduledNotificationsUseCaseImpl(
  val scheduledNotificationDbPort: ScheduledNotificationDbPort,
  val notificationPreferencesDbService: NotificationPreferencesDbService,
) : CreateScheduledNotificationsUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  private val messages =
    listOf(
      "Не забудьте о своих целях на сегодня! 🎯",
      "Время сделать шаг к своей цели! 💪",
      "Прогресс — это путь, а не пункт назначения! 🚀",
      "Вы уже сегодня работали над своими целями? ⏰",
      "Маленькие шаги ведут к большим результатам! 🌟",
    )

  override fun createForAllUsers() {
    log.info("Creating scheduled notifications for all users")
    val currentDateTime = OffsetDateTime.now(ZoneOffset.UTC)
    val allPreferences = notificationPreferencesDbService.findAll()
    log.debug("Found {} notification preferences", allPreferences.size)

    val processedCount =
      allPreferences
        .filter { it.notificationsPerDay > 0 }
        .onEach { prefs ->
          log.debug(
            "Processing prefs for user={}, count={}, start={}, end={}",
            prefs.userId,
            prefs.notificationsPerDay,
            prefs.startDayTime,
            prefs.endDayTime,
          )

          prefs.userId?.let { userId ->
            createNextNotification(
              userId,
              prefs.startDayTime,
              prefs.endDayTime,
              prefs.notificationsPerDay,
              currentDateTime,
            )
          }
        }
        .count()

    log.info("Created scheduled notifications for {} users", processedCount)
  }

  private fun createNextNotification(
    userId: UUID,
    startTime: OffsetTime,
    endTime: OffsetTime,
    notificationsPerDay: Int,
    currentDateTime: OffsetDateTime,
  ) {
    val nextDateTimeSlot =
      calculateNextDateTimeSlot(startTime, endTime, notificationsPerDay, currentDateTime)

    if (nextDateTimeSlot == null) {
      log.debug("No more slots for user={} today", userId)
      return
    }

    if (nextDateTimeSlot.isBefore(currentDateTime)) {
      log.debug("Slot {} is in the past for user={}, skipping", nextDateTimeSlot, userId)
      return
    }

    if (scheduledNotificationDbPort.existsByUserIdAndScheduledAt(userId, nextDateTimeSlot)) {
      log.debug("Notification already exists for user={} at {}", userId, nextDateTimeSlot)
      return
    }

    val message = messages.random()
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val notification =
      ScheduledNotification().apply {
        this.id = UUID.randomUUID()
        this.userId = userId
        this.scheduledAt = nextDateTimeSlot
        this.message = message
        this.status = NotificationStatus.PENDING
        this.retryCount = 0
        this.createdAt = now
        this.updatedAt = now
      }

    scheduledNotificationDbPort.save(notification)
    log.info(
      "Created scheduled notification for user={} at {}: {}",
      userId,
      nextDateTimeSlot,
      message,
    )
  }

  private fun calculateNextDateTimeSlot(
    startTime: OffsetTime,
    endTime: OffsetTime,
    count: Int,
    currentDateTime: OffsetDateTime,
  ): OffsetDateTime? {
    if (count <= 0) return null

    val currentLocalDate = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate()
    val startDateTime = OffsetDateTime.of(currentLocalDate, startTime.toLocalTime(), ZoneOffset.UTC)
    val endDateTime = OffsetDateTime.of(currentLocalDate, endTime.toLocalTime(), ZoneOffset.UTC)
    if (startDateTime.isAfter(endDateTime)) {
      endDateTime.plusDays(1)
    }
    val totalMinutes = Duration.between(startDateTime, endDateTime).toMinutes()
    log.debug("totalMinutes={}, startTime={}, endTime={}", totalMinutes, startTime, endTime)
    if (totalMinutes <= 0) return null

    val middleDateTime = startDateTime.plusMinutes(totalMinutes / 2)

    val slots: List<OffsetDateTime> =
      when (count) {
        1 -> listOf(middleDateTime)
        2 -> listOf(startDateTime, endDateTime)
        3 -> listOf(startDateTime, middleDateTime, endDateTime)
        else -> {
          val step = totalMinutes / (count - 1)
          (0 until count).map { i -> startDateTime.plusMinutes(step * i) }
        }
      }
    log.debug("Calculated slots: {}, currentTime={}", slots, currentDateTime)

    val result = slots.filter { it.isAfter(currentDateTime) }.minByOrNull { it }
    log.debug("Next slot: {}", result)
    return result
  }
}
