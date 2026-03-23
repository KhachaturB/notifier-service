package ru.vachoo.notifier.adapter.`in`.jobs

import java.util.logging.Logger
import org.quartz.Job
import org.quartz.JobExecutionContext
import ru.vachoo.notifier.domain.entities.Reminder

class ReminderJob : Job {

  companion object {
    val log: Logger = Logger.getLogger(ReminderJob::class.java.name)
  }

  override fun execute(context: JobExecutionContext?) {
    val reminder = context?.jobDetail?.jobDataMap?.get("reminder") as Reminder
    log.info(reminder.text)
  }
}
