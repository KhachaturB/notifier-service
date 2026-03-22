package ru.vachoo.notifier.application.services

import java.util.UUID
import org.springframework.stereotype.Service
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.domain.entities.User

@Service
class TokenValidationService(val userDbPort: UserDbPort) {

  fun validateOrCreateUser(userId: UUID, userToken: String): Boolean {
    val existingUser = userDbPort.findById(userId)
    return if (existingUser == null) {
      val newUser =
        User().apply {
          this.id = userId
          this.userToken = userToken
        }
      userDbPort.saveUser(newUser)
      true
    } else {
      existingUser.userToken == userToken
    }
  }
}
