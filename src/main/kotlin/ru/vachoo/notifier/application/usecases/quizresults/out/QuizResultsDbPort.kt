package ru.vachoo.notifier.application.usecases.quizresults.out

import java.util.UUID
import ru.vachoo.notifier.domain.entities.QuizResult

interface QuizResultsDbPort {
  fun saveQuizResult(quizResult: QuizResult)

  fun findByUserId(userId: UUID): QuizResult?
}
