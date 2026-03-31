package ru.vachoo.notifier.application.usecases.quizresults

import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
import ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort
import ru.vachoo.notifier.domain.entities.QuizResult

@ExtendWith(MockitoExtension::class)
class GetQuizResultUseCaseImplTest {

  @Mock private lateinit var quizResultsDbPort: QuizResultsDbPort

  @Mock private lateinit var userDbPort: UserDbPort

  private lateinit var useCase: GetQuizResultUseCaseImpl

  private val userId = UUID.randomUUID()
  private val validToken = "valid-token"

  @BeforeEach
  fun setUp() {
    useCase = GetQuizResultUseCaseImpl(quizResultsDbPort, userDbPort)
  }

  @Test
  fun shouldReturnQuizResult_WhenValidToken() {
    val quizResult = QuizResult()
    quizResult.userId = userId
    quizResult.answers = listOf(1, 2, 3)
    quizResult.primaryGoal = "Lose weight"
    quizResult.motivationStyle = "achievement"

    whenever(userDbPort.findUserTokenById(userId)).thenReturn(validToken)
    whenever(quizResultsDbPort.findByUserId(org.mockito.kotlin.any())).thenReturn(quizResult)

    val result = useCase.get(userId, validToken)

    assertNotNull(result)
    val captor = argumentCaptor<UUID>()
    verify(quizResultsDbPort).findByUserId(captor.capture())
    assertEquals(userId, captor.firstValue)
    assertEquals(userId, result!!.userId)
    assertEquals(listOf(1, 2, 3), result.answers)
    assertEquals("Lose weight", result.primaryGoal)
    assertEquals("achievement", result.motivationStyle)
  }

  @Test
  fun shouldReturnNull_WhenQuizResultNotFound() {
    whenever(userDbPort.findUserTokenById(userId)).thenReturn(validToken)
    whenever(quizResultsDbPort.findByUserId(userId)).thenReturn(null)

    val result = useCase.get(userId, validToken)

    assert(result == null)
    verify(quizResultsDbPort).findByUserId(userId)
  }

  @Test
  fun shouldThrowForbidden_WhenUserNotFound() {
    whenever(userDbPort.findUserTokenById(userId)).thenReturn(null)

    assertThrows<ForbiddenException> { useCase.get(userId, validToken) }
  }

  @Test
  fun shouldThrowForbidden_WhenInvalidToken() {
    whenever(userDbPort.findUserTokenById(userId)).thenReturn("different-token")

    assertThrows<ForbiddenException> { useCase.get(userId, validToken) }
  }
}
