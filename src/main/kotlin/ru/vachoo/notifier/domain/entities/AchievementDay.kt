package ru.vachoo.notifier.domain.entities

import java.time.OffsetDateTime
import java.util.UUID

class AchievementDay {
  var id: UUID? = null
  var userId: UUID? = null
  var userToken: String? = null
  var date: OffsetDateTime = OffsetDateTime.now()
}
