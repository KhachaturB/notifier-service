package ru.vachoo.notifier.application.usecases.setnotificationpreference

import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.adapter.`in`.web.dtos.NotificationPreferenceDto
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

  @Transactional
  override fun set(preferenceId: UUID, dto: NotificationPreferenceDto) {
    val userId = dto.userId ?: throw IllegalArgumentException("userId is required")
    val userToken = dto.userToken ?: throw IllegalArgumentException("userToken is required")
    val startDayTime =
      dto.startDayTime ?: throw IllegalArgumentException("startDayTime is required")
    val endDayTime = dto.endDayTime ?: throw IllegalArgumentException("endDayTime is required")
    val notificationsPerDay =
      dto.notificationsPerDay ?: throw IllegalArgumentException("notificationsPerDay is required")

    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      throw UnauthorizedException("Invalid user token")
    }
    val preference =
      NotificationPreference().apply {
        this.id = preferenceId
        this.userId = userId
        this.startDayTime = startDayTime
        this.endDayTime = endDayTime
        this.notificationsPerDay = notificationsPerDay
      }
    setNotificationPreferenceDbPort.saveNotificationPreference(preference)
  }
}
