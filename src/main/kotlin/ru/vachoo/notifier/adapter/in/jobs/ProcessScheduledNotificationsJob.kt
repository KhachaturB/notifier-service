package ru.vachoo.notifier.adapter.`in`.jobs

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.usecases.processschedsnotifs.`in`.ProcessScheduledNotificationsUseCase

@Component
class ProcessScheduledNotificationsJob(
  private val processScheduledNotificationsUseCase: ProcessScheduledNotificationsUseCase
) : Job {

  override fun execute(context: JobExecutionContext?) {
    processScheduledNotificationsUseCase.process()
  }
}
