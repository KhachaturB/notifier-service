package ru.vachoo.notifier.domain.entities

import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class NotificationPreference {
  var id: UUID? = null
  var userId: UUID? = null
  var userToken: String? = null
  var startDayTime: LocalTime = LocalTime.of(9, 0)
  var endDayTime: LocalTime = LocalTime.of(21, 0)
  var notificationsPerDay: Int = 5
  var timezone: String = "UTC"
  var createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
  var updatedAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
}
