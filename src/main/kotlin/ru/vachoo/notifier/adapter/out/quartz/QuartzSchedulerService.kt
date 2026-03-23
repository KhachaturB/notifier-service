package ru.vachoo.notifier.adapter.out.quartz

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.`in`.jobs.ReminderJob
import ru.vachoo.notifier.application.usecases.setreminder.out.SchedulerPort
import ru.vachoo.notifier.domain.entities.Reminder

@Component
class QuartzSchedulerService(val scheduler: Scheduler) : SchedulerPort {

  override fun schedule(reminder: Reminder) {
    val job =
      JobBuilder.newJob(ReminderJob::class.java)
        .setJobData(JobDataMap(mapOf("reminder" to reminder)))
        .build()
    val trigger =
      TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule(reminder.schedule))
        .build()
    scheduler.scheduleJob(job, trigger)
  }
}
