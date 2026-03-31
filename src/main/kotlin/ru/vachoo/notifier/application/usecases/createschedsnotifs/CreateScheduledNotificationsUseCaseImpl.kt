package ru.vachoo.notifier.application.usecases.createschedsnotifs

import java.time.Duration
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.usecases.createschedsnotifs.`in`.CreateScheduledNotificationsUseCase
import ru.vachoo.notifier.application.usecases.createschedsnotifs.out.NotificationPreferencesDbPort
import ru.vachoo.notifier.domain.entities.ScheduledNotification
import ru.vachoo.notifier.domain.enums.NotificationStatus

@Component
class CreateScheduledNotificationsUseCaseImpl(
  val scheduledNotificationDbPort: ScheduledNotificationDbPort,
  val notificationPreferencesDbPort: NotificationPreferencesDbPort,
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
    val nowUtc = OffsetDateTime.now(ZoneOffset.UTC)
    val allPreferences = notificationPreferencesDbPort.findAll()
    log.debug("Found {} notification preferences", allPreferences.size)

    var totalCreated = 0
    for (prefs in allPreferences) {
      if (prefs.notificationsPerDay <= 0) continue

      val zoneId = ZoneId.of(prefs.timezone)
      val nowInUserTz = ZonedDateTime.now(zoneId)

      log.info(
        "Processing prefs for user={}, count={}, start={}, end={}, timezone={}",
        prefs.userId,
        prefs.notificationsPerDay,
        prefs.startDayTime,
        prefs.endDayTime,
        prefs.timezone,
      )

      val userId = prefs.userId ?: continue
      val created =
        createNextNotification(
          userId,
          prefs.startDayTime,
          prefs.endDayTime,
          prefs.notificationsPerDay,
          nowInUserTz,
          nowUtc,
        )
      if (created) totalCreated++
    }

    log.info("Created {} scheduled notifications for all users", totalCreated)
  }

private fun createNextNotification(
        userId: UUID,
        startTime: LocalTime,
        endTime: LocalTime,
        count: Int,
        nowInUserTz: ZonedDateTime,
        nowUtc: OffsetDateTime,
    ): Boolean {
        val todaySlots = calculateSlots(startTime, endTime, count, nowInUserTz)

        val nextSlot = todaySlots.filter { it.isAfter(nowInUserTz) }.minByOrNull { it }
            ?: run {
                val tomorrow = nowInUserTz.plusDays(1)
                val tomorrowSlots = calculateSlots(startTime, endTime, count, tomorrow)
                tomorrowSlots.minByOrNull { it }
            }

        if (nextSlot == null) {
            log.debug("No upcoming slots for user={}", userId)
            return false
        }

        val scheduledAtUtc = nextSlot.withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime()

    if (scheduledNotificationDbPort.existsByUserIdAndScheduledAt(userId, scheduledAtUtc)) {
      log.debug("Notification already exists for user={} at {}", userId, scheduledAtUtc)
      return false
    }

    val message = messages.random()
    val notification =
      ScheduledNotification().apply {
        this.id = UUID.randomUUID()
        this.userId = userId
        this.scheduledAt = scheduledAtUtc
        this.message = message
        this.status = NotificationStatus.PENDING
        this.retryCount = 0
        this.createdAt = nowUtc
        this.updatedAt = nowUtc
      }

    scheduledNotificationDbPort.save(notification)
    log.info(
      "Created scheduled notification for user={} at {} (user tz) -> {} UTC: {}",
      userId,
      nextSlot,
      scheduledAtUtc,
      message,
    )
    return true
  }

  private fun calculateSlots(
    startTime: LocalTime,
    endTime: LocalTime,
    count: Int,
    nowInUserTz: ZonedDateTime,
  ): List<ZonedDateTime> {
    if (count <= 0) return emptyList()

    val now = nowInUserTz.toLocalDate()
    val zone = nowInUserTz.getZone()

    val startDateTime = ZonedDateTime.of(now, startTime, zone)
    var endDateTime = ZonedDateTime.of(now, endTime, zone)

    if (endTime.isBefore(startTime) || endTime == startTime) {
      endDateTime = endDateTime.plusDays(1)
    }

    val totalMinutes = Duration.between(startDateTime, endDateTime).toMinutes()
    log.debug(
      "totalMinutes={}, startDateTime={}, endDateTime={}",
      totalMinutes,
      startDateTime,
      endDateTime,
    )
    if (totalMinutes <= 0) return emptyList()

    val slots = mutableListOf<ZonedDateTime>()
    when (count) {
      1 -> slots.add(startDateTime.plusMinutes(totalMinutes / 2))
      2 -> {
        slots.add(startDateTime)
        slots.add(endDateTime)
      }
      3 -> {
        slots.add(startDateTime)
        slots.add(startDateTime.plusMinutes(totalMinutes / 2))
        slots.add(endDateTime)
      }
      else -> {
        val step = totalMinutes.toDouble() / (count - 1)
        for (i in 0 until count) {
          slots.add(startDateTime.plusMinutes((step * i).toLong()))
        }
      }
    }

    log.debug("Calculated slots: {}", slots)
    return slots
  }
}
