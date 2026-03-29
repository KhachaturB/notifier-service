package ru.vachoo.notifier.domain.entities

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class User {
  var id: UUID? = null
  var userToken: String = ""
  var username: String = ""
  var apnsToken: String? = null
  var createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
  var updatedAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
}
