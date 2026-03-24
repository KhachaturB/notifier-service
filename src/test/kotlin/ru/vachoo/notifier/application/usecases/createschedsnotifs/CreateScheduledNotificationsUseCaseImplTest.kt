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
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.usecases.createschedsnotifs.out.NotificationPreferencesDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateScheduledNotificationsUseCaseImplTest {

  @Mock private lateinit var scheduledNotificationDbPort: ScheduledNotificationDbPort

  @Mock private lateinit var notificationPreferencesDbPort: NotificationPreferencesDbPort

  private lateinit var useCase: CreateScheduledNotificationsUseCaseImpl

  private val userId = UUID.randomUUID()

  @BeforeEach
  fun setUp() {
    useCase =
      CreateScheduledNotificationsUseCaseImpl(
        scheduledNotificationDbPort,
        notificationPreferencesDbPort,
      )
  }

  @Test
  fun shouldNotCreate_WhenNoPreferencesExist() {
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(emptyList())

    useCase.createForAllUsers()
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
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(true)

    useCase.createForAllUsers()
  }

  @Test
  fun shouldCreateNotification_WhenValidPreference() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 5
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(false)

    useCase.createForAllUsers()
  }

  @Test
  fun shouldCalculateSlots_WhenCountIsOne() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 1
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(false)

    useCase.createForAllUsers()
  }

  @Test
  fun shouldCalculateSlots_WhenCountIsTwo() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 2
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(false)

    useCase.createForAllUsers()
  }

  @Test
  fun shouldCalculateSlots_WhenCountIsThree() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 3
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(false)

    useCase.createForAllUsers()
  }

  @Test
  fun shouldCalculateSlots_WhenCountIsGreaterThanThree() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 5
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(false)

    useCase.createForAllUsers()
  }

  @Test
  fun shouldHandleEndTimeBeforeStartTime() {
    val preference =
      NotificationPreference().apply {
        this.userId = userId
        this.notificationsPerDay = 2
        this.startDayTime = LocalTime.of(22, 0)
        this.endDayTime = LocalTime.of(6, 0)
        this.timezone = "UTC"
      }
    whenever(notificationPreferencesDbPort.findAll()).thenReturn(listOf(preference))
    whenever(scheduledNotificationDbPort.existsByUserIdAndScheduledAt(any(), any()))
      .thenReturn(false)

    useCase.createForAllUsers()
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
  }
}
