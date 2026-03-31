package ru.vachoo.notifier.application.usecases.quizresults

import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.application.exceptions.ForbiddenException
import ru.vachoo.notifier.application.usecases.quizresults.`in`.SaveQuizResultUseCase
import ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort
import ru.vachoo.notifier.domain.entities.QuizResult

@ExtendWith(MockitoExtension::class)
class SaveQuizResultUseCaseImplTest {

  @Mock private lateinit var quizResultsDbPort: QuizResultsDbPort

  @Mock private lateinit var userDbPort: UserDbPort

  private lateinit var useCase: SaveQuizResultUseCase

  private val userId = UUID.randomUUID()
  private val validToken = "valid-token"

  @BeforeEach
  fun setUp() {
    useCase = SaveQuizResultUseCaseImpl(quizResultsDbPort, userDbPort)
  }

  @Test
  fun shouldSaveQuizResult_WhenValidToken() {
    val quizResult = QuizResult()
    quizResult.answers = listOf(1, 2, 3)
    quizResult.primaryGoal = "Lose weight"
    quizResult.motivationStyle = "achievement"

    whenever(userDbPort.findUserTokenById(userId)).thenReturn(validToken)

    useCase.save(userId, validToken, quizResult)

    val captor = argumentCaptor<QuizResult>()
    verify(quizResultsDbPort).saveQuizResult(captor.capture())

    val savedResult = captor.firstValue
    assertEquals(userId, savedResult.userId)
    assertEquals(listOf(1, 2, 3), savedResult.answers)
    assertEquals("Lose weight", savedResult.primaryGoal)
    assertEquals("achievement", savedResult.motivationStyle)
  }

  @Test
  fun shouldThrowForbidden_WhenUserNotFound() {
    val quizResult = QuizResult()
    whenever(userDbPort.findUserTokenById(userId)).thenReturn(null)

    assertThrows<ForbiddenException> { useCase.save(userId, validToken, quizResult) }
  }

  @Test
  fun shouldThrowForbidden_WhenInvalidToken() {
    val quizResult = QuizResult()
    whenever(userDbPort.findUserTokenById(userId)).thenReturn("different-token")

    assertThrows<ForbiddenException> { useCase.save(userId, validToken, quizResult) }
  }
}
