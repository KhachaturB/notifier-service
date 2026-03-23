package ru.vachoo.notifier.adapter.`in`.jobs.config

import org.quartz.spi.TriggerFiredBundle
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import org.springframework.stereotype.Component

@Component
class AutowiringSpringBeanJobFactory : SpringBeanJobFactory(), ApplicationContextAware {

  private var applicationContext: ApplicationContext? = null

  override fun setApplicationContext(context: ApplicationContext) {
    applicationContext = context
  }

  override fun createJobInstance(bundle: TriggerFiredBundle): Any {
    val jobClass = bundle.jobDetail.jobClass
    return applicationContext!!.getBean(jobClass)
  }
}
