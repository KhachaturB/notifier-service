package ru.vachoo.notifier.application.usecases.getreminders.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.Reminder

interface GetRemindersUseCase {

  fun getReminders(userId: UUID): List<Reminder>
}
