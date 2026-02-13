package ru.vachoo.notifier.adapter.out.db.reminder

import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.generated.tables.references.REMINDERS
import ru.vachoo.notifier.application.commonports.out.ReminderDbPort
import ru.vachoo.notifier.domain.entities.Reminder

@Component
class ReminderDbService(val dslContext: DSLContext) : ReminderDbPort {

  override fun saveReminder(reminder: Reminder) {
    dslContext
      .insertInto(REMINDERS)
      .columns(REMINDERS.ID, REMINDERS.USERID, REMINDERS.TEXT, REMINDERS.SCHEDULE)
      .values(reminder.id, reminder.userId, reminder.text, reminder.schedule)
      .onDuplicateKeyUpdate()
      .set(REMINDERS.USERID, reminder.userId)
      .set(REMINDERS.TEXT, reminder.text)
      .set(REMINDERS.SCHEDULE, reminder.schedule)
      .execute()
  }

  override fun getRemindersByUser(userId: UUID): List<Reminder> =
    dslContext
      .select()
      .from(REMINDERS)
      .where(REMINDERS.USERID.eq(userId))
      .fetchInto(Reminder::class.java)
}
