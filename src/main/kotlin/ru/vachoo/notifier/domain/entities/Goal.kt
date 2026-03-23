package ru.vachoo.notifier.domain.entities

import java.util.UUID
import ru.vachoo.notifier.domain.enums.GoalCategory

class Goal {
  var id: UUID? = null
  var userId: UUID? = null
  var userToken: String? = null
  var name: String = ""
  var category: GoalCategory = GoalCategory.PERSONAL
  var icon: String = ""
  var color: String = "#000000"
  var measurement: Measurement = Measurement()
  var isDeleted: Boolean = false
}
