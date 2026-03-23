package ru.vachoo.notifier.application.usecases.setnotificationpreference

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.setnotificationpreference.`in`.SetNotificationPreferenceUseCase
import ru.vachoo.notifier.application.usecases.setnotificationpreference.out.SetNotificationPreferenceDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@Component
class SetNotificationPreferenceUseCaseImpl(
  val tokenValidationService: TokenValidationService,
  val setNotificationPreferenceDbPort: SetNotificationPreferenceDbPort,
) : SetNotificationPreferenceUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  @Transactional
  override fun set(preferenceId: UUID, preference: NotificationPreference) {
    val userId = preference.userId ?: throw IllegalArgumentException("userId is required")
    val userToken = preference.userToken ?: throw IllegalArgumentException("userToken is required")

    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      log.warn("Invalid token for user: userId={}", userId)
      throw UnauthorizedException("Invalid user token")
    }

    log.info("Setting notification preference for userId={}", userId)
    setNotificationPreferenceDbPort.saveNotificationPreference(preference)
    log.info("Notification preference saved for userId={}, preferenceId={}", userId, preference.id)
  }
}
