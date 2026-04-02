package ru.vachoo.notifier.adapter.`in`.jobs

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.services.MotivationalMessagesService

@Component
class GenerateMotivationalMessagesJob(
    val motivationalMessagesService: MotivationalMessagesService
) : Job {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(context: JobExecutionContext) {
        log.info("Starting motivational messages generation job")
        motivationalMessagesService.generateMessages()
        log.info("Motivational messages generation job completed")
    }
}
