package ru.vachoo.notifier.adapter.`in`.web.dtos

data class SaveUserDto(
  var userToken: String = "",
  var username: String = "",
  var apnsToken: String? = null,
  var quizAnswers: List<Int>? = null,
)
