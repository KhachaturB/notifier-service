package ru.vachoo.notifier.domain.enums

enum class NotificationStatus {
  PENDING,
  SENDING,
  SENT,
  FAILED,
  EXHAUSTED,
  CANCELLED,
}
