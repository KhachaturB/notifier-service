package ru.vachoo.notifier.domain.entities

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import ru.vachoo.notifier.domain.enums.NotificationStatus

class ScheduledNotification {
  var id: UUID? = null
  var userId: UUID? = null
  var status: NotificationStatus = NotificationStatus.PENDING
  var message: String = ""
  var scheduledAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
  var retryCount: Int = 0
  var nextRetryAt: OffsetDateTime? = null
  var createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
  var updatedAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
  var sentAt: OffsetDateTime? = null
}
