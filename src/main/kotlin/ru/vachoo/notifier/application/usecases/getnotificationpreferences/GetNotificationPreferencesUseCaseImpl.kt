package ru.vachoo.notifier.application.usecases.getnotificationpreferences

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.`in`.GetNotificationPreferencesUseCase
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.out.GetNotificationPreferencesDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@Component
class GetNotificationPreferencesUseCaseImpl(
  val tokenValidationService: TokenValidationService,
  val getNotificationPreferencesDbPort: GetNotificationPreferencesDbPort,
) : GetNotificationPreferencesUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun getPreferences(userId: UUID, userToken: String): List<NotificationPreference> {
    log.info("Getting notification preferences: userId={}", userId)
    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      log.warn("Invalid token for user: userId={}", userId)
      throw UnauthorizedException("Invalid user token")
    }
    val preferences = getNotificationPreferencesDbPort.findByUserId(userId)
    log.info("Found {} preferences for user: userId={}", preferences.size, userId)
    return preferences
  }
}
