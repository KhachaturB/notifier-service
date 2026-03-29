package ru.vachoo.notifier.domain.entities

import java.util.UUID

class QuizResult {
  var userId: UUID? = null
  var answers: List<Int>? = null
  var primaryGoal: String = ""
  var motivationStyle: String = ""
}
