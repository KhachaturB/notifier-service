package ru.vachoo.notifier.adapter.`in`.web.dtos

data class QuizResultResponseDto(
    var answers: List<Int>? = null,
    var primaryGoal: String = "",
    var motivationStyle: String = "",
)
