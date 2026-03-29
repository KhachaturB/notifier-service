package ru.vachoo.notifier.domain.entities

import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import ru.vachoo.notifier.domain.enums.NotificationStatus

class UserTest {

  @Test
  fun shouldHaveDefaultValues() {
    val user = User()

    assertNull(user.id)
    assertEquals("", user.userToken)
    assertEquals("", user.username)
    assertNull(user.apnsToken)
    assertNotNull(user.createdAt)
    assertNotNull(user.updatedAt)
  }

  @Test
  fun shouldAllowSettingAllFields() {
    val id = UUID.randomUUID()
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val user =
      User().apply {
        this.id = id
        this.userToken = "token123"
        this.username = "john"
        this.apnsToken = "apns-token-abc"
        this.createdAt = now
        this.updatedAt = now
      }

    assertEquals(id, user.id)
    assertEquals("token123", user.userToken)
    assertEquals("john", user.username)
    assertEquals("apns-token-abc", user.apnsToken)
    assertEquals(now, user.createdAt)
    assertEquals(now, user.updatedAt)
  }

  @Test
  fun shouldAllowSettingApnsTokenToNull() {
    val user =
      User().apply {
        this.apnsToken = "some-token"
        this.apnsToken = null
      }

    assertNull(user.apnsToken)
  }
}

class NotificationPreferenceTest {

  @Test
  fun shouldHaveDefaultValues() {
    val preference = NotificationPreference()

    assertNull(preference.id)
    assertNull(preference.userId)
    assertNull(preference.userToken)
    assertEquals(LocalTime.of(9, 0), preference.startDayTime)
    assertEquals(LocalTime.of(21, 0), preference.endDayTime)
    assertEquals(5, preference.notificationsPerDay)
    assertEquals("UTC", preference.timezone)
  }

  @Test
  fun shouldAllowSettingAllFields() {
    val id = UUID.randomUUID()
    val userId = UUID.randomUUID()
    val preference =
      NotificationPreference().apply {
        this.id = id
        this.userId = userId
        this.userToken = "test-token"
        this.startDayTime = LocalTime.of(8, 0)
        this.endDayTime = LocalTime.of(20, 0)
        this.notificationsPerDay = 3
        this.timezone = "Europe/Moscow"
      }

    assertEquals(id, preference.id)
    assertEquals(userId, preference.userId)
    assertEquals("test-token", preference.userToken)
    assertEquals(LocalTime.of(8, 0), preference.startDayTime)
    assertEquals(LocalTime.of(20, 0), preference.endDayTime)
    assertEquals(3, preference.notificationsPerDay)
    assertEquals("Europe/Moscow", preference.timezone)
  }
}

class ScheduledNotificationTest {

  @Test
  fun shouldHaveDefaultValues() {
    val notification = ScheduledNotification()

    assertNull(notification.id)
    assertNull(notification.userId)
    assertEquals(NotificationStatus.PENDING, notification.status)
    assertEquals("", notification.message)
    assertNotNull(notification.scheduledAt)
    assertEquals(0, notification.retryCount)
    assertNull(notification.nextRetryAt)
    assertNotNull(notification.createdAt)
    assertNotNull(notification.updatedAt)
    assertNull(notification.sentAt)
  }

  @Test
  fun shouldAllowSettingAllFields() {
    val id = UUID.randomUUID()
    val userId = UUID.randomUUID()
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val notification =
      ScheduledNotification().apply {
        this.id = id
        this.userId = userId
        this.status = NotificationStatus.SENT
        this.message = "Test message"
        this.scheduledAt = now
        this.retryCount = 3
        this.nextRetryAt = now.plusMinutes(5)
        this.createdAt = now
        this.updatedAt = now
        this.sentAt = now
      }

    assertEquals(id, notification.id)
    assertEquals(userId, notification.userId)
    assertEquals(NotificationStatus.SENT, notification.status)
    assertEquals("Test message", notification.message)
    assertEquals(now, notification.scheduledAt)
    assertEquals(3, notification.retryCount)
    assertEquals(now.plusMinutes(5), notification.nextRetryAt)
    assertEquals(now, notification.createdAt)
    assertEquals(now, notification.updatedAt)
    assertEquals(now, notification.sentAt)
  }

  @Test
  fun shouldSupportAllStatusTransitions() {
    val notification = ScheduledNotification()

    notification.status = NotificationStatus.PENDING
    assertEquals(NotificationStatus.PENDING, notification.status)

    notification.status = NotificationStatus.SENDING
    assertEquals(NotificationStatus.SENDING, notification.status)

    notification.status = NotificationStatus.SENT
    assertEquals(NotificationStatus.SENT, notification.status)

    notification.status = NotificationStatus.FAILED
    assertEquals(NotificationStatus.FAILED, notification.status)

    notification.status = NotificationStatus.EXHAUSTED
    assertEquals(NotificationStatus.EXHAUSTED, notification.status)
  }
}
