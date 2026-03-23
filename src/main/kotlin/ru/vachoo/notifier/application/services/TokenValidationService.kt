package ru.vachoo.notifier.application.services

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.domain.entities.User

@Service
class TokenValidationService(val userDbPort: UserDbPort) {

  private val log = LoggerFactory.getLogger(javaClass)

  fun validateOrCreateUser(userId: UUID, userToken: String): Boolean {
    log.debug("Validating token for user: userId={}", userId)
    val existingUser = userDbPort.findById(userId)
    return if (existingUser == null) {
      log.info("Creating new user: userId={}", userId)
      val newUser =
        User().apply {
          this.id = userId
          this.userToken = userToken
        }
      userDbPort.saveUser(newUser)
      true
    } else {
      val isValid = existingUser.userToken == userToken
      if (!isValid) {
        log.warn("Token mismatch for user: userId={}", userId)
      }
      isValid
    }
  }
}
