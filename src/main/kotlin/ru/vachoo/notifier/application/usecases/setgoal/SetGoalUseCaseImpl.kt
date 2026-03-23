package ru.vachoo.notifier.application.usecases.setgoal

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.services.TokenValidationService
import ru.vachoo.notifier.application.usecases.setgoal.`in`.SetGoalUseCase
import ru.vachoo.notifier.application.usecases.setgoal.out.SetGoalDbPort
import ru.vachoo.notifier.domain.entities.Goal

@Component
class SetGoalUseCaseImpl(
  val tokenValidationService: TokenValidationService,
  val setGoalDbPort: SetGoalDbPort,
) : SetGoalUseCase {

  private val log = LoggerFactory.getLogger(javaClass)

  @Transactional
  override fun set(goalId: UUID, goal: Goal) {
    log.info("Setting goal: goalId={}, userId={}", goalId, goal.userId)
    val userId = goal.userId ?: throw IllegalArgumentException("userId is required")
    val userToken = goal.userToken ?: throw IllegalArgumentException("userToken is required")

    if (!tokenValidationService.validateOrCreateUser(userId, userToken)) {
      log.warn("Invalid token for user: userId={}", userId)
      throw UnauthorizedException("Invalid user token")
    }
    goal.id = goalId
    setGoalDbPort.saveGoal(goal)
    log.info("Goal saved: goalId={}, userId={}", goalId, userId)
  }
}
