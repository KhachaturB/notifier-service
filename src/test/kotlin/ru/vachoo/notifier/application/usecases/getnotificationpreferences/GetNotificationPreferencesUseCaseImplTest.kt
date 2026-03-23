package ru.vachoo.notifier.application.usecases.getnotificationpreferences

import java.time.OffsetTime
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
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.out.GetNotificationPreferencesDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@ExtendWith(MockitoExtension::class)
class GetNotificationPreferencesUseCaseImplTest {

  @Mock private lateinit var tokenValidationService: TokenValidationService

  @Mock private lateinit var getNotificationPreferencesDbPort: GetNotificationPreferencesDbPort

  private lateinit var useCase: GetNotificationPreferencesUseCaseImpl

  private val userId = UUID.randomUUID()
  private val userToken = "test-token"

  @BeforeEach
  fun setUp() {
    useCase =
      GetNotificationPreferencesUseCaseImpl(
        tokenValidationService,
        getNotificationPreferencesDbPort,
      )
  }

  @Test
  fun shouldReturnPreferences_WhenTokenIsValid() {
    val preferenceId = UUID.randomUUID()
    val preference =
      NotificationPreference().apply {
        this.id = preferenceId
        this.userId = userId
        this.startDayTime = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC)
        this.endDayTime = OffsetTime.of(21, 0, 0, 0, ZoneOffset.UTC)
        this.notificationsPerDay = 5
      }
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(getNotificationPreferencesDbPort.findByUserId(userId)).thenReturn(listOf(preference))

    val result = useCase.getPreferences(userId, userToken)

    assertThat(result).hasSize(1)
    verify(getNotificationPreferencesDbPort).findByUserId(userId)
  }

  @Test
  fun shouldThrowUnauthorized_WhenTokenIsInvalid() {
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(false)

    assertThatThrownBy { useCase.getPreferences(userId, userToken) }
      .isInstanceOf(UnauthorizedException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldReturnEmptyList_WhenNoPreferencesExist() {
    whenever(tokenValidationService.validateOrCreateUser(userId, userToken)).thenReturn(true)
    whenever(getNotificationPreferencesDbPort.findByUserId(userId)).thenReturn(emptyList())

    val result = useCase.getPreferences(userId, userToken)

    assertThat(result).isEmpty()
  }
}
