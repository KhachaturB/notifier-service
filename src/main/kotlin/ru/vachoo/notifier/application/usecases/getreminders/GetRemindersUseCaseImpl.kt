package ru.vachoo.notifier.application.usecases.getreminders

import java.util.UUID
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.commonports.out.ReminderDbPort
import ru.vachoo.notifier.application.usecases.getreminders.`in`.GetRemindersUseCase
import ru.vachoo.notifier.domain.entities.Reminder

@Component
class GetRemindersUseCaseImpl(val reminderDbPort: ReminderDbPort) : GetRemindersUseCase {

  override fun getReminders(userId: UUID): List<Reminder> {
    return reminderDbPort.getRemindersByUser(userId)
  }
}
