package ru.vachoo.notifier.application.commonports.out

import java.util.UUID
import ru.vachoo.notifier.domain.entities.Reminder

interface ReminderDbPort {

  fun saveReminder(reminder: Reminder)

  fun getRemindersByUser(userId: UUID): List<Reminder>
}
