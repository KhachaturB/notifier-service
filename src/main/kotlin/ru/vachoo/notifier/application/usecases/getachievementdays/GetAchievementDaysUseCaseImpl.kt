package ru.vachoo.notifier.application.usecases.getachievementdays

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.getachievementdays.`in`.GetAchievementDaysUseCase
import ru.vachoo.notifier.application.usecases.getachievementdays.out.GetAchievementDaysDbPort
import ru.vachoo.notifier.domain.entities.AchievementDay

@Component
class GetAchievementDaysUseCaseImpl(
  val tokenValidationService: TokenValidationService,
  val getAchievementDaysDbPort: GetAchievementDaysDbPort,
) : GetAchievementDaysUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun getAchievementDays(userId: UUID, userToken: String): List<AchievementDay> {
    log.info("Getting achievement days: userId={}", userId)
    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      log.warn("Invalid token for user: userId={}", userId)
      throw UnauthorizedException("Invalid user token")
    }
    val achievementDays = getAchievementDaysDbPort.findByUserId(userId)
    log.info("Found {} achievement days for user: userId={}", achievementDays.size, userId)
    return achievementDays
  }
}
