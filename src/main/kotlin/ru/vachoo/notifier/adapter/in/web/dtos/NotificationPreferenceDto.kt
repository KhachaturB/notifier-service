package ru.vachoo.notifier.adapter.`in`.web.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime
import java.util.UUID

data class NotificationPreferenceDto(
  var id: UUID? = null,
  var userId: UUID? = null,
  var userToken: String? = null,
  @param:JsonFormat(pattern = "HH:mm:ss")
  @param:Schema(
    description = "Start day time in user's timezone",
    type = "string",
    format = "time",
    example = "08:15:00",
  )
  var startDayTime: LocalTime? = null,
  @param:JsonFormat(pattern = "HH:mm:ss")
  @param:Schema(
    description = "End day time in user's timezone",
    type = "string",
    format = "time",
    example = "15:30:00",
  )
  var endDayTime: LocalTime? = null,
  var notificationsPerDay: Int? = null,
  var timezone: String? = null,
)
