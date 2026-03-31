package ru.vachoo.notifier.application.usecases.setnotificationpreference

import java.time.LocalTime
import java.util.UUID
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.setnotificationpreference.out.SetNotificationPreferenceDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@ExtendWith(MockitoExtension::class)
class SetNotificationPreferenceUseCaseImplTest {

  @Mock private lateinit var tokenValidationService: TokenValidationService

  @Mock private lateinit var setNotificationPreferenceDbPort: SetNotificationPreferenceDbPort

  @Mock private lateinit var scheduledNotificationDbPort: ScheduledNotificationDbPort

  private lateinit var useCase: SetNotificationPreferenceUseCaseImpl

  private val preferenceId = UUID.randomUUID()
  private val userId = UUID.randomUUID()
  private val userToken = "test-token"

  @BeforeEach
  fun setUp() {
    useCase =
      SetNotificationPreferenceUseCaseImpl(
        tokenValidationService,
        setNotificationPreferenceDbPort,
        scheduledNotificationDbPort,
      )
  }

  @Test
  fun shouldSavePreference_WhenValidInput() {
    val preference = NotificationPreference()
    preference.userId = userId
    preference.userToken = userToken
    preference.startDayTime = LocalTime.of(9, 0)
    preference.endDayTime = LocalTime.of(21, 0)
    preference.notificationsPerDay = 5
    preference.timezone = "Europe/Moscow"
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)

    useCase.set(preferenceId, preference)

    verify(scheduledNotificationDbPort).cancelPendingByUserId(userId)
  }

  @Test
  fun shouldThrowUnauthorized_WhenTokenInvalid() {
    val preference = NotificationPreference()
    preference.userId = userId
    preference.userToken = userToken
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(false)

    assertThatThrownBy { useCase.set(preferenceId, preference) }
      .isInstanceOf(UnauthorizedException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenUserIdIsNull() {
    val preference = NotificationPreference()
    preference.userToken = userToken

    assertThatThrownBy { useCase.set(preferenceId, preference) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userId is required")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenUserTokenIsNull() {
    val preference = NotificationPreference()
    preference.userId = userId

    assertThatThrownBy { useCase.set(preferenceId, preference) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userToken is required")
  }
}
