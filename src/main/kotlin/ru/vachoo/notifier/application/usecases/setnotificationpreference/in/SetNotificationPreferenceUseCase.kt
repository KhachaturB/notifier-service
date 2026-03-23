package ru.vachoo.notifier.application.usecases.setnotificationpreference.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.NotificationPreference

interface SetNotificationPreferenceUseCase {
  fun set(preferenceId: UUID, preference: NotificationPreference)
}
