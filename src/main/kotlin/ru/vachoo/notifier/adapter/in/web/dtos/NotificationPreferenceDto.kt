package ru.vachoo.notifier.adapter.`in`.web.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalTime
import java.util.UUID

data class NotificationPreferenceDto(
  var id: UUID? = null,
  var userId: UUID? = null,
  var userToken: String? = null,
  @JsonFormat(pattern = "HH:mm") var startDayTime: LocalTime? = null,
  @JsonFormat(pattern = "HH:mm") var endDayTime: LocalTime? = null,
  var notificationsPerDay: Int? = null,
)
