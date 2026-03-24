package ru.vachoo.notifier.adapter.`in`.jobs.config

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.`in`.jobs.CreateScheduledNotificationsJob
import ru.vachoo.notifier.adapter.`in`.jobs.ProcessScheduledNotificationsJob

@Component
class NotificationSchedulerConfig(
  private val scheduler: Scheduler,
  private val jobFactory: AutowiringSpringBeanJobFactory,
) {

  private val log = LoggerFactory.getLogger(javaClass)

  @EventListener(ApplicationReadyEvent::class)
  fun scheduleJobs() {
    scheduler.setJobFactory(jobFactory)
    log.info("Scheduling notification jobs")
    scheduleCreateNotificationsJob()
    scheduleProcessNotificationsJob()
    log.info("Notification jobs scheduled")
  }

  private fun scheduleCreateNotificationsJob() {
    val job =
      JobBuilder.newJob(CreateScheduledNotificationsJob::class.java)
        .withIdentity("create-scheduled-notifications-job")
        .storeDurably()
        .build()

    val trigger =
      TriggerBuilder.newTrigger()
        .withIdentity("create-scheduled-notifications-trigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?"))
        .build()

    scheduler.scheduleJob(job, trigger)
    log.info("Scheduled CreateScheduledNotificationsJob")
  }

  private fun scheduleProcessNotificationsJob() {
    val job =
      JobBuilder.newJob(ProcessScheduledNotificationsJob::class.java)
        .withIdentity("process-scheduled-notifications-job")
        .storeDurably()
        .build()

    val trigger =
      TriggerBuilder.newTrigger()
        .withIdentity("process-scheduled-notifications-trigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?"))
        .build()

    scheduler.scheduleJob(job, trigger)
    log.info("Scheduled ProcessScheduledNotificationsJob")
  }
}
