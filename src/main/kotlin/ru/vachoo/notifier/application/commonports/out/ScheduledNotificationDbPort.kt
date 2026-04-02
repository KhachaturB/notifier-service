package ru.vachoo.notifier.application.commonports.out

import java.time.OffsetDateTime
import java.util.UUID
import ru.vachoo.notifier.domain.entities.ScheduledNotification

interface ScheduledNotificationDbPort {
  fun save(scheduledNotification: ScheduledNotification)

  fun findPendingForProcessing(limit: Int): List<ScheduledNotification>

  fun existsByUserIdAndScheduledAtAndActiveStatuses(
    userId: UUID,
    scheduledAt: OffsetDateTime,
  ): Boolean

  fun findByUserId(userId: UUID): List<ScheduledNotification>

  fun cancelPendingByUserId(userId: UUID)
}
