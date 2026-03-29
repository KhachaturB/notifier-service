package ru.vachoo.notifier.adapter.`in`.web.dtos

data class QuizResultDto(
  var answers: List<Int>? = null,
  var primaryGoal: String = "",
  var motivationStyle: String = "",
)
