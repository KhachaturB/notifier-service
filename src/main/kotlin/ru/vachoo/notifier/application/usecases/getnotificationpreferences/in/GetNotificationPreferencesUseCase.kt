package ru.vachoo.notifier.application.usecases.getnotificationpreferences.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.NotificationPreference

interface GetNotificationPreferencesUseCase {
  fun getPreferences(userId: UUID, userToken: String): List<NotificationPreference>
}
