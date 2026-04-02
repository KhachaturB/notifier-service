package ru.vachoo.notifier.application.usecases.createschedsnotifs

import java.time.LocalTime
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.services.MotivationalMessagesService
import ru.vachoo.notifier.application.usecases.createschedsnotifs.out.NotificationPreferencesDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateScheduledNotificationsUseCaseImplTest {

@Mock private lateinit var scheduledNotificationDbPort: ScheduledNotificationDbPort

    @Mock private lateinit var notificationPreferencesDbPort: NotificationPreferencesDbPort

    @Mock private lateinit var motivationalMessagesService: MotivationalMessagesService

    private lateinit var useCase: CreateScheduledNotificationsUseCaseImpl

  private val userId = UUID.randomUUID()

@BeforeEach
    fun setUp() {
        useCase =
            CreateScheduledNotificationsUseCaseImpl(
                scheduledNotificationDbPort,
                notificationPreferencesDbPort,
                motivationalMessagesService,
            )
        whenever(motivationalMessagesService.getRandomMessage()).thenReturn("Test message")
    }

  @Test
  fun shouldNotCreate_WhenNoPreferencesExist() {
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(emptyList())

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldSkipUser_WhenNotificationsPerDayIsZero() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 0
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldSkipUser_WhenNotificationsPerDayIsNegative() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = -1
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldSkipUser_WhenUserIdIsNull() {
    val preference =
      NotificationPreference().apply {
        this.userId = null
        this.notificationsPerDay = 5
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldNotCreate_WhenNotificationAlreadyExists() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 5
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAtAndActiveStatuses(any(), any()))
      .thenReturn(true)

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldSkipWhenTotalMinutesIsZero() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 2
        this.startDayTime = LocalTime.of(12, 0)
        this.endDayTime = LocalTime.of(12, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldNotCreate_WhenAllSlotsAreInThePast() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 5
        this.startDayTime = LocalTime.of(0, 0)
        this.endDayTime = LocalTime.of(1, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))

    useCase.createForAllUsers()

    verify(scheduledNotificationDbPort, never()).save(any())
  }
}
