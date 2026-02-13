package ru.vachoo.notifier.domain.entities

import java.util.UUID

class Reminder {
  var id: UUID? = null
  var schedule: String = ""
  var userId: UUID? = null
  var text: String = ""
}
