package ru.vachoo.notifier.application.usecases.getnotificationpreferences

import java.util.UUID
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

  override fun getPreferences(userId: UUID, userToken: String): List<NotificationPreference> {
    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      throw UnauthorizedException("Invalid user token")
    }
    return getNotificationPreferencesDbPort.findByUserId(userId)
  }
}
