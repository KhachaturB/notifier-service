package ru.vachoo.notifier.application.usecases.getachievementdays

import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.getachievementdays.out.GetAchievementDaysDbPort
import ru.vachoo.notifier.domain.entities.AchievementDay

@ExtendWith(MockitoExtension::class)
class GetAchievementDaysUseCaseImplTest {

  @Mock private lateinit var tokenValidationService: TokenValidationService

  @Mock private lateinit var getAchievementDaysDbPort: GetAchievementDaysDbPort

  private lateinit var useCase: GetAchievementDaysUseCaseImpl

  private val userId = UUID.randomUUID()
  private val userToken = "test-token"

  @BeforeEach
  fun setUp() {
    useCase = GetAchievementDaysUseCaseImpl(tokenValidationService, getAchievementDaysDbPort)
  }

  @Test
  fun shouldReturnAchievementDays_WhenTokenIsValid() {
    val achievementDayId = UUID.randomUUID()
    val achievementDay =
      AchievementDay().apply {
        this.id = achievementDayId
        this.userId = userId
        this.date = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC)
      }
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(getAchievementDaysDbPort.findByUserId(userId)).thenReturn(listOf(achievementDay))

    val result = useCase.getAchievementDays(userId, userToken)

    assertThat(result).hasSize(1)
    verify(getAchievementDaysDbPort).findByUserId(userId)
  }

  @Test
  fun shouldThrowUnauthorized_WhenTokenIsInvalid() {
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(false)

    assertThatThrownBy { useCase.getAchievementDays(userId, userToken) }
      .isInstanceOf(UnauthorizedException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldReturnEmptyList_WhenNoAchievementDaysExist() {
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(getAchievementDaysDbPort.findByUserId(userId)).thenReturn(emptyList())

    val result = useCase.getAchievementDays(userId, userToken)

    assertThat(result).isEmpty()
  }
}
