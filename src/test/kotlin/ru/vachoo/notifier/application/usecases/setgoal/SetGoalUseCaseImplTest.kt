package ru.vachoo.notifier.application.usecases.setgoal

import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.setgoal.out.SetGoalDbPort
import ru.vachoo.notifier.domain.entities.Goal
import ru.vachoo.notifier.domain.enums.GoalCategory

@ExtendWith(MockitoExtension::class)
class SetGoalUseCaseImplTest {

  @Mock private lateinit var tokenValidationService: TokenValidationService

  @Mock private lateinit var setGoalDbPort: SetGoalDbPort

  private lateinit var useCase: SetGoalUseCaseImpl

  private val goalId = UUID.randomUUID()
  private val userId = UUID.randomUUID()
  private val userToken = "test-token"

  @BeforeEach
  fun setUp() {
    useCase = SetGoalUseCaseImpl(tokenValidationService, setGoalDbPort)
  }

  @Test
  fun shouldSaveGoal_WhenValidInput() {
    val goal = Goal()
    goal.userId = userId
    goal.userToken = userToken
    goal.name = "Test Goal"
    goal.category = GoalCategory.PERSONAL
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)

    useCase.set(goalId, goal)

    assertThat(goal.id).isEqualTo(goalId)
  }

  @Test
  fun shouldThrowUnauthorized_WhenTokenInvalid() {
    val goal = Goal()
    goal.userId = userId
    goal.userToken = userToken
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(false)

    assertThatThrownBy { useCase.set(goalId, goal) }
      .isInstanceOf(UnauthorizedException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenUserIdIsNull() {
    val goal = Goal()
    goal.userToken = userToken

    assertThatThrownBy { useCase.set(goalId, goal) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userId is required")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenUserTokenIsNull() {
    val goal = Goal()
    goal.userId = userId

    assertThatThrownBy { useCase.set(goalId, goal) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userToken is required")
  }
}
