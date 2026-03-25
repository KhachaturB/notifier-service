package ru.vachoo.notifier.application.usecases.setuser.`in`

import ru.vachoo.notifier.domain.entities.User

interface SetUserUseCase {
  fun set(user: User)
}
