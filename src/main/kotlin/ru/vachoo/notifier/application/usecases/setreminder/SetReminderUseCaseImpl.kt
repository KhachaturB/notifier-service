package ru.vachoo.notifier.application.usecases.setreminder

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.commonports.out.ReminderDbPort
import ru.vachoo.notifier.application.usecases.setreminder.`in`.SetReminderUseCase
import ru.vachoo.notifier.application.usecases.setreminder.out.SchedulerPort
import ru.vachoo.notifier.domain.entities.Reminder

@Component
class SetReminderUseCaseImpl(val reminderDbPort: ReminderDbPort, val schedulerPort: SchedulerPort) :
  SetReminderUseCase {

  @Transactional
  override fun set(reminder: Reminder) {
    reminderDbPort.saveReminder(reminder)
    schedulerPort.schedule(reminder)
  }
}
