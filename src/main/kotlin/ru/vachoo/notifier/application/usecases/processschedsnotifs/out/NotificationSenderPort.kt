package ru.vachoo.notifier.application.usecases.processschedsnotifs.out

import java.util.UUID

interface NotificationSenderPort {
  fun send(userId: UUID, message: String): Boolean
}
