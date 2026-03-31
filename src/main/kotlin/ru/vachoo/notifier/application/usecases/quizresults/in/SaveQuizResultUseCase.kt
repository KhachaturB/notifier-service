package ru.vachoo.notifier.application.usecases.quizresults.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.QuizResult

interface SaveQuizResultUseCase {
  fun save(userId: UUID, userToken: String, quizResult: QuizResult)
}
