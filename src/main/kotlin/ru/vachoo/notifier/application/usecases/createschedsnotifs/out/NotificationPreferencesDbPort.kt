package ru.vachoo.notifier.application.usecases.createschedsnotifs.out

import ru.vachoo.notifier.domain.entities.NotificationPreference

interface NotificationPreferencesDbPort {
  fun findAll(): List<NotificationPreference>
}
