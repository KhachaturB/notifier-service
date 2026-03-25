package ru.vachoo.notifier.domain.entities

import java.time.Instant
import java.util.UUID

class User {
  var id: UUID? = null
  var userToken: String = ""
  var username: String = ""
  var apnsToken: String? = null
  var createdAt: Instant? = null
  var updatedAt: Instant? = null
}
