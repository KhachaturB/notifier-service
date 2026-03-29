package ru.vachoo.notifier.application.usecases.quizresults

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.application.usecases.quizresults.`in`.SaveQuizResultUseCase
import ru.vachoo.notifier.domain.entities.QuizResult

@Component
class SaveQuizResultUseCaseImpl(
  private val quizResultsDbPort:
    ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort
) : SaveQuizResultUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  @Transactional
  override fun save(userId: UUID, quizResult: QuizResult) {
    log.info("Saving quiz result for userId={}", userId)
    quizResult.userId = userId
    quizResultsDbPort.saveQuizResult(quizResult)
    log.info("Quiz result saved successfully for userId={}", userId)
  }
}
