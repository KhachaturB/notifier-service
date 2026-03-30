package ru.vachoo.notifier.application.usecases.quizresults

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.application.exceptions.ForbiddenException
import ru.vachoo.notifier.application.usecases.quizresults.`in`.GetQuizResultUseCase
import ru.vachoo.notifier.domain.entities.QuizResult

@Component
class GetQuizResultUseCaseImpl(
    private val quizResultsDbPort:
        ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort,
    private val userDbPort: UserDbPort
) : GetQuizResultUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun get(userId: UUID, userToken: String): QuizResult? {
        log.info("Getting quiz result for userId={}", userId)

        val existingToken = userDbPort.findUserTokenById(userId)
        if (existingToken == null) {
            log.warn("User not found for userId={}", userId)
            throw ForbiddenException("User not found")
        }
        if (existingToken != userToken) {
            log.warn("Token mismatch for userId={}", userId)
            throw ForbiddenException("Invalid user token")
        }

        val quizResult = quizResultsDbPort.findByUserId(userId)
        if (quizResult != null) {
            log.info("Quiz result found for userId={}", userId)
        } else {
            log.info("Quiz result not found for userId={}", userId)
        }
        return quizResult
    }
}
