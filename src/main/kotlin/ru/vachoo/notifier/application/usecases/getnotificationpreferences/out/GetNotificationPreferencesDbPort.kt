package ru.vachoo.notifier.application.usecases.getnotificationpreferences.out

import java.util.UUID
import ru.vachoo.notifier.domain.entities.NotificationPreference

interface GetNotificationPreferencesDbPort {
  fun findByUserId(userId: UUID): List<NotificationPreference>

  fun findAll(): List<NotificationPreference>
}
