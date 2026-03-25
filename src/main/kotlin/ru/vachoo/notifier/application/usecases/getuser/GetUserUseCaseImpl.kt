package ru.vachoo.notifier.application.usecases.getuser

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.application.usecases.getuser.`in`.GetUserUseCase
import ru.vachoo.notifier.domain.entities.User

@Component
class GetUserUseCaseImpl(val userDbPort: UserDbPort) : GetUserUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun get(userId: UUID): User? {
    log.info("Getting user by userId={}", userId)
    val user = userDbPort.findById(userId)?.apply { this.userToken = "" }
    if (user != null) {
      log.info("User found userId={}", userId)
    } else {
      log.info("User not found userId={}", userId)
    }
    return user
  }
}
