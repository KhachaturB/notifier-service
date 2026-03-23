package ru.vachoo.notifier.domain.entities

import java.time.OffsetTime
import java.time.ZoneOffset
import java.util.UUID

class NotificationPreference {
  var id: UUID? = null
  var userId: UUID? = null
  var userToken: String? = null
  var startDayTime: OffsetTime = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC)
  var endDayTime: OffsetTime = OffsetTime.of(21, 0, 0, 0, ZoneOffset.UTC)
  var notificationsPerDay: Int = 5
}
