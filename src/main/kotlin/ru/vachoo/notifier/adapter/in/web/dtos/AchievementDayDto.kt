package ru.vachoo.notifier.adapter.`in`.web.dtos

import java.time.OffsetDateTime
import java.util.UUID

data class AchievementDayDto(
  var id: UUID? = null,
  var userId: UUID? = null,
  var userToken: String? = null,
  var date: OffsetDateTime? = null,
)
