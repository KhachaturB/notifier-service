package ru.vachoo.notifier.application.usecases.getgoals

import java.time.OffsetDateTime
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.getachievementdays.out.GetAchievementDaysDbPort
import ru.vachoo.notifier.application.usecases.getgoals.`in`.GetGoalsUseCase
import ru.vachoo.notifier.application.usecases.getgoals.out.GetGoalsDbPort
import ru.vachoo.notifier.application.usecases.getgoals.out.SetAchievementDayDbPort
import ru.vachoo.notifier.domain.entities.AchievementDay
import ru.vachoo.notifier.domain.entities.Goal

@Component
class GetGoalsUseCaseImpl(
  val tokenValidationService: TokenValidationService,
  val getGoalsDbPort: GetGoalsDbPort,
  val getAchievementDaysDbPort: GetAchievementDaysDbPort,
  val setAchievementDayDbPort: SetAchievementDayDbPort,
) : GetGoalsUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun getGoals(userId: UUID, userToken: String): List<Goal> {
    log.info("Getting goals: userId={}", userId)
    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      log.warn("Invalid token for user: userId={}", userId)
      throw UnauthorizedException("Invalid user token")
    }

    val today = OffsetDateTime.now()
    if (!getAchievementDaysDbPort.hasAchievementDayForDate(userId, today)) {
      log.info("Adding achievement day for today: userId={}, date={}", userId, today)
      val achievementDay =
        AchievementDay().apply {
          this.id = UUID.randomUUID()
          this.userId = userId
          this.date = today
        }
      setAchievementDayDbPort.saveAchievementDay(achievementDay)
    }

    val goals = getGoalsDbPort.findByUserId(userId)
    log.info("Found {} goals for user: userId={}", goals.size, userId)
    return goals
  }
}
