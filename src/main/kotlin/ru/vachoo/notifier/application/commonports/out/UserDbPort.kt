package ru.vachoo.notifier.application.commonports.out

import java.util.UUID
import ru.vachoo.notifier.domain.entities.User

interface UserDbPort {
  fun findById(userId: UUID): User?

  fun saveUser(user: User)

  fun existsById(userId: UUID): Boolean
}
