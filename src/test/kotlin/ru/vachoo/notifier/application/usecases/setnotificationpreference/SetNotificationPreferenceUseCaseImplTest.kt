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
import ru.vachoo.notifier.adapter.`in`.web.dtos.NotificationPreferenceDto
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.setnotificationpreference.out.SetNotificationPreferenceDbPort

@ExtendWith(MockitoExtension::class)
class SetNotificationPreferenceUseCaseImplTest {

  @Mock private lateinit var tokenValidationService: TokenValidationService

  @Mock private lateinit var setNotificationPreferenceDbPort: SetNotificationPreferenceDbPort

  private lateinit var useCase: SetNotificationPreferenceUseCaseImpl

  private val preferenceId = UUID.randomUUID()
  private val userId = UUID.randomUUID()
  private val userToken = "test-token"
  private val startDayTime = LocalTime.of(9, 0)
  private val endDayTime = LocalTime.of(21, 0)
  private val notificationsPerDay = 5

  @BeforeEach
  fun setUp() {
    useCase =
      SetNotificationPreferenceUseCaseImpl(tokenValidationService, setNotificationPreferenceDbPort)
  }

  @Test
  fun shouldSavePreference_WhenValidInput() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = userToken,
        startDayTime = startDayTime,
        endDayTime = endDayTime,
        notificationsPerDay = notificationsPerDay,
      )
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)

    useCase.set(preferenceId, dto)

    verify(setNotificationPreferenceDbPort)
      .saveNotificationPreference(
        org.mockito.kotlin.check<ru.vachoo.notifier.domain.entities.NotificationPreference> { pref
          ->
          org.assertj.core.api.Assertions.assertThat(pref.id).isEqualTo(preferenceId)
          org.assertj.core.api.Assertions.assertThat(pref.userId).isEqualTo(userId)
          org.assertj.core.api.Assertions.assertThat(pref.startDayTime).isEqualTo(startDayTime)
          org.assertj.core.api.Assertions.assertThat(pref.endDayTime).isEqualTo(endDayTime)
          org.assertj.core.api.Assertions.assertThat(pref.notificationsPerDay)
            .isEqualTo(notificationsPerDay)
        }
      )
  }

  @Test
  fun shouldThrowUnauthorized_WhenTokenInvalid() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = userToken,
        startDayTime = startDayTime,
        endDayTime = endDayTime,
        notificationsPerDay = notificationsPerDay,
      )
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(false)

    assertThatThrownBy { useCase.set(preferenceId, dto) }
      .isInstanceOf(UnauthorizedException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenUserIdIsNull() {
    val dto =
      NotificationPreferenceDto(
        userId = null,
        userToken = userToken,
        startDayTime = startDayTime,
        endDayTime = endDayTime,
        notificationsPerDay = notificationsPerDay,
      )

    assertThatThrownBy { useCase.set(preferenceId, dto) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userId is required")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenUserTokenIsNull() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = null,
        startDayTime = startDayTime,
        endDayTime = endDayTime,
        notificationsPerDay = notificationsPerDay,
      )

    assertThatThrownBy { useCase.set(preferenceId, dto) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userToken is required")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenStartDayTimeIsNull() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = userToken,
        startDayTime = null,
        endDayTime = endDayTime,
        notificationsPerDay = notificationsPerDay,
      )

    assertThatThrownBy { useCase.set(preferenceId, dto) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("startDayTime is required")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenEndDayTimeIsNull() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = userToken,
        startDayTime = startDayTime,
        endDayTime = null,
        notificationsPerDay = notificationsPerDay,
      )

    assertThatThrownBy { useCase.set(preferenceId, dto) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("endDayTime is required")
  }

  @Test
  fun shouldThrowIllegalArgument_WhenNotificationsPerDayIsNull() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = userToken,
        startDayTime = startDayTime,
        endDayTime = endDayTime,
        notificationsPerDay = null,
      )

    assertThatThrownBy { useCase.set(preferenceId, dto) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("notificationsPerDay is required")
  }
}
