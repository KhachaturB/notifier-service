package ru.vachoo.notifier.application.usecases.getgoals

import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.getachievementdays.out.GetAchievementDaysDbPort
import ru.vachoo.notifier.application.usecases.getgoals.out.GetGoalsDbPort
import ru.vachoo.notifier.application.usecases.getgoals.out.SetAchievementDayDbPort
import ru.vachoo.notifier.domain.entities.Goal
import ru.vachoo.notifier.domain.enums.GoalCategory

@ExtendWith(MockitoExtension::class)
class GetGoalsUseCaseImplTest {

  @Mock private lateinit var tokenValidationService: TokenValidationService

  @Mock private lateinit var getGoalsDbPort: GetGoalsDbPort

  @Mock private lateinit var getAchievementDaysDbPort: GetAchievementDaysDbPort

  @Mock private lateinit var setAchievementDayDbPort: SetAchievementDayDbPort

  private lateinit var useCase: GetGoalsUseCaseImpl

  private val userId = UUID.randomUUID()
  private val userToken = "test-token"

  @BeforeEach
  fun setUp() {
    useCase =
      GetGoalsUseCaseImpl(
        tokenValidationService,
        getGoalsDbPort,
        getAchievementDaysDbPort,
        setAchievementDayDbPort,
      )
  }

  @Test
  fun shouldReturnGoals_WhenTokenIsValid() {
    val goalId = UUID.randomUUID()
    val goal =
      Goal().apply {
        this.id = goalId
        this.userId = userId
        this.name = "Test Goal"
        this.category = GoalCategory.PERSONAL
      }
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(
        getAchievementDaysDbPort.hasAchievementDayForDate(
          org.mockito.kotlin.any(),
          org.mockito.kotlin.any(),
        )
      )
      .thenReturn(true)
    whenever(getGoalsDbPort.findByUserId(userId)).thenReturn(listOf(goal))

    val result = useCase.getGoals(userId, userToken)

    assertThat(result).hasSize(1)
    verify(getGoalsDbPort).findByUserId(userId)
  }

  @Test
  fun shouldThrowUnauthorized_WhenTokenIsInvalid() {
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(false)

    assertThatThrownBy { useCase.getGoals(userId, userToken) }
      .isInstanceOf(UnauthorizedException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldReturnEmptyList_WhenNoGoalsExist() {
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(
        getAchievementDaysDbPort.hasAchievementDayForDate(
          org.mockito.kotlin.any(),
          org.mockito.kotlin.any(),
        )
      )
      .thenReturn(true)
    whenever(getGoalsDbPort.findByUserId(userId)).thenReturn(emptyList())

    val result = useCase.getGoals(userId, userToken)

    assertThat(result).isEmpty()
  }

  @Test
  fun shouldAddAchievementDay_WhenNotExistsForToday() {
    val goalId = UUID.randomUUID()
    val goal =
      Goal().apply {
        this.id = goalId
        this.userId = userId
        this.name = "Test Goal"
        this.category = GoalCategory.PERSONAL
      }
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(
        getAchievementDaysDbPort.hasAchievementDayForDate(
          org.mockito.kotlin.any(),
          org.mockito.kotlin.any(),
        )
      )
      .thenReturn(false)
    whenever(getGoalsDbPort.findByUserId(userId)).thenReturn(listOf(goal))

    val result = useCase.getGoals(userId, userToken)

    verify(setAchievementDayDbPort).saveAchievementDay(org.mockito.kotlin.any())
    assertThat(result).hasSize(1)
  }

  @Test
  fun shouldNotAddAchievementDay_WhenAlreadyExistsForToday() {
    val goalId = UUID.randomUUID()
    val goal =
      Goal().apply {
        this.id = goalId
        this.userId = userId
        this.name = "Test Goal"
        this.category = GoalCategory.PERSONAL
      }
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(
        getAchievementDaysDbPort.hasAchievementDayForDate(
          org.mockito.kotlin.any(),
          org.mockito.kotlin.any(),
        )
      )
      .thenReturn(true)
    whenever(getGoalsDbPort.findByUserId(userId)).thenReturn(listOf(goal))

    useCase.getGoals(userId, userToken)

    verify(setAchievementDayDbPort, never()).saveAchievementDay(org.mockito.kotlin.any())
  }
}
