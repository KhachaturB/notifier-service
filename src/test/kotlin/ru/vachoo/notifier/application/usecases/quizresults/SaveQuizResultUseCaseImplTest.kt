package ru.vachoo.notifier.application.usecases.quizresults

import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import ru.vachoo.notifier.application.usecases.quizresults.`in`.SaveQuizResultUseCase
import ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort
import ru.vachoo.notifier.domain.entities.QuizResult

@ExtendWith(MockitoExtension::class)
class SaveQuizResultUseCaseImplTest {

  @Mock private lateinit var quizResultsDbPort: QuizResultsDbPort

  private lateinit var useCase: SaveQuizResultUseCase

  private val userId = UUID.randomUUID()

  @BeforeEach
  fun setUp() {
    useCase = SaveQuizResultUseCaseImpl(quizResultsDbPort)
  }

  @Test
  fun shouldSaveQuizResult_WhenValidInput() {
    val quizResult = QuizResult()
    quizResult.answers = listOf(1, 2, 3)
    quizResult.primaryGoal = "Lose weight"
    quizResult.motivationStyle = "achievement"

    useCase.save(userId, quizResult)

    val captor = argumentCaptor<QuizResult>()
    verify(quizResultsDbPort).saveQuizResult(captor.capture())

    val savedResult = captor.firstValue
    assertNotNull(savedResult.userId)
    assertEquals(userId, savedResult.userId)
    assertEquals(listOf(1, 2, 3), savedResult.answers)
    assertEquals("Lose weight", savedResult.primaryGoal)
    assertEquals("achievement", savedResult.motivationStyle)
  }

  @Test
  fun shouldSetUserId_WhenSaving() {
    val quizResult = QuizResult()
    quizResult.answers = listOf(1)
    quizResult.primaryGoal = "Build muscle"
    quizResult.motivationStyle = "growth"

    useCase.save(userId, quizResult)

    assertEquals(userId, quizResult.userId)
    verify(quizResultsDbPort).saveQuizResult(quizResult)
  }
}
