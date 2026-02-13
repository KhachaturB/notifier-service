package ru.vachoo.notifier.adapter.`in`.web.dtos

import java.util.UUID

data class ReminderDto(
  var id: UUID? = null,
  var schedule: String = "",
  var userId: UUID? = null,
  var text: String = "",
)
