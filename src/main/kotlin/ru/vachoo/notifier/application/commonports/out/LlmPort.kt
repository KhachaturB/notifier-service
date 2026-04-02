package ru.vachoo.notifier.application.commonports.out

interface LlmPort {
  fun generateMotivationalMessages(count: Int): List<String>
}
