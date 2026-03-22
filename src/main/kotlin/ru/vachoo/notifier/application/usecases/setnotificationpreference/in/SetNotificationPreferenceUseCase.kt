package ru.vachoo.notifier.application.usecases.setnotificationpreference.`in`

import java.util.UUID
import ru.vachoo.notifier.adapter.`in`.web.dtos.NotificationPreferenceDto

interface SetNotificationPreferenceUseCase {
  fun set(preferenceId: UUID, dto: NotificationPreferenceDto)
}
