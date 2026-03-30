package ru.vachoo.notifier.application.usecases.quizresults

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.application.exceptions.ForbiddenException
import ru.vachoo.notifier.application.usecases.quizresults.`in`.SaveQuizResultUseCase
import ru.vachoo.notifier.domain.entities.QuizResult

@Component
class SaveQuizResultUseCaseImpl(
    private val quizResultsDbPort:
        ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort,
    private val userDbPort: UserDbPort
) : SaveQuizResultUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun save(userId: UUID, userToken: String, quizResult: QuizResult) {
        log.info("Saving quiz result for userId={}", userId)

        val existingToken = userDbPort.findUserTokenById(userId)
        if (existingToken == null) {
            log.warn("User not found for userId={}", userId)
            throw ForbiddenException("User not found")
        }
        if (existingToken != userToken) {
            log.warn("Token mismatch for userId={}", userId)
            throw ForbiddenException("Invalid user token")
        }

        quizResult.userId = userId
        quizResultsDbPort.saveQuizResult(quizResult)
        log.info("Quiz result saved successfully for userId={}", userId)
    }
}
