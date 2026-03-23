package ru.vachoo.notifier.application.usecases.setnotificationpreference.out

import java.util.UUID
import ru.vachoo.notifier.domain.entities.NotificationPreference

interface SetNotificationPreferenceDbPort {
  fun saveNotificationPreference(preference: NotificationPreference)

  fun findById(preferenceId: UUID): NotificationPreference?

  fun findByUserIdSingle(userId: UUID): NotificationPreference?
}
