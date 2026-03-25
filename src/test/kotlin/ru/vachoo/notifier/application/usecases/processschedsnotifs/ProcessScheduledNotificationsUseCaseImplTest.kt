package ru.vachoo.notifier.application.usecases.processschedsnotifs

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.application.usecases.processschedsnotifs.out.NotificationSenderPort
import ru.vachoo.notifier.domain.entities.ScheduledNotification
import ru.vachoo.notifier.domain.enums.NotificationStatus

@ExtendWith(MockitoExtension::class)
class ProcessScheduledNotificationsUseCaseImplTest {

  @Mock private lateinit var scheduledNotificationDbPort: ScheduledNotificationDbPort

  @Mock private lateinit var notificationSenderPort: NotificationSenderPort

  private lateinit var useCase: ProcessScheduledNotificationsUseCaseImpl

  private val userId = UUID.randomUUID()

  @BeforeEach
  fun setUp() {
    useCase =
      ProcessScheduledNotificationsUseCaseImpl(scheduledNotificationDbPort, notificationSenderPort)
  }

  @Test
  fun shouldNotProcess_WhenNoNotifications() {
    whenever(scheduledNotificationDbPort.findPendingForProcessing(100)).thenReturn(emptyList())

    useCase.process()

    verify(scheduledNotificationDbPort, never()).save(any())
  }

  @Test
  fun shouldSkipNotification_WhenUserIdIsNull() {
    val notification =
      ScheduledNotification().apply {
        this.id = UUID.randomUUID()
        this.userId = null
        this.status = NotificationStatus.PENDING
        this.message = "Test message"
        this.scheduledAt = OffsetDateTime.now(ZoneOffset.UTC)
        this.retryCount = 0
      }
    whenever(scheduledNotificationDbPort.findPendingForProcessing(100))
      .thenReturn(listOf(notification))

    useCase.process()

    verify(scheduledNotificationDbPort, never()).save(any())
  }
}
