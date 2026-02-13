package ru.vachoo.notifier

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class NotifierService

fun main(args: Array<String>) {
  runApplication<NotifierService>(*args)
}
