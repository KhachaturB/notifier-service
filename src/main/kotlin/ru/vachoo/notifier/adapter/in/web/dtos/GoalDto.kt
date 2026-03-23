package ru.vachoo.notifier.adapter.`in`.web.dtos

import java.util.UUID
import ru.vachoo.notifier.domain.enums.GoalCategory

data class GoalDto(
  var id: UUID? = null,
  var userId: UUID? = null,
  var userToken: String? = null,
  var name: String? = null,
  var category: GoalCategory? = null,
  var icon: String? = null,
  var color: String? = null,
  var measurement: MeasurementDto? = null,
  var isDeleted: Boolean? = null,
)

data class MeasurementDto(
  var currentValue: Double? = null,
  var targetValue: Double? = null,
  var unit: String? = null,
)
