package ru.vachoo.notifier.application.usecases.setreminder.out

import ru.vachoo.notifier.domain.entities.Reminder

interface SchedulerPort {

  fun schedule(reminder: Reminder)
}
