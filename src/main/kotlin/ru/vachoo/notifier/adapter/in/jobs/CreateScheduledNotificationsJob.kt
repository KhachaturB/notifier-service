package ru.vachoo.notifier.adapter.`in`.jobs

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.usecases.createschedsnotifs.`in`.CreateScheduledNotificationsUseCase

@Component
class CreateScheduledNotificationsJob(
  private val createScheduledNotificationsUseCase: CreateScheduledNotificationsUseCase
) : Job {

  override fun execute(context: JobExecutionContext?) {
    createScheduledNotificationsUseCase.createForAllUsers()
  }
}
