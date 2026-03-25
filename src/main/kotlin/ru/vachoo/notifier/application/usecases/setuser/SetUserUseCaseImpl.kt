package ru.vachoo.notifier.application.usecases.setuser

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.application.usecases.setuser.`in`.SetUserUseCase
import ru.vachoo.notifier.domain.entities.User

@Component
class SetUserUseCaseImpl(val userDbPort: UserDbPort) : SetUserUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  @Transactional
  override fun set(user: User) {
    val userId = user.id ?: throw IllegalArgumentException("userId is required")
    log.info("Processing user set request for userId={}", userId)

    val existingToken = userDbPort.findUserTokenById(userId)

    if (existingToken != null) {
      if (existingToken != user.userToken) {
        log.warn("Token mismatch for userId={}", userId)
        throw ForbiddenException("Invalid user token")
      }
      log.info("User exists, updating userId={}", userId)
    } else {
      log.info("User does not exist, creating userId={}", userId)
    }

    userDbPort.saveUser(user)
    log.info("User saved successfully userId={}", userId)
  }
}

class ForbiddenException(message: String) : RuntimeException(message)
