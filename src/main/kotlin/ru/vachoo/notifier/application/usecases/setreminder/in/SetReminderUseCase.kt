package ru.vachoo.notifier.application.usecases.setreminder.`in`

import ru.vachoo.notifier.domain.entities.Reminder

interface SetReminderUseCase {

  fun set(reminder: Reminder)
}
