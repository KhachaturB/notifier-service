package ru.vachoo.notifier.application.usecases.getuser.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.User

interface GetUserUseCase {
  fun get(userId: UUID): User?
}
