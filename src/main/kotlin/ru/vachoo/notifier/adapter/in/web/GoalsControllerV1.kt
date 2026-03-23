package ru.vachoo.notifier.adapter.`in`.web

import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.vachoo.notifier.adapter.`in`.web.dtos.GoalDto
import ru.vachoo.notifier.adapter.`in`.web.dtos.MeasurementDto
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.usecases.getgoals.`in`.GetGoalsUseCase
import ru.vachoo.notifier.application.usecases.setgoal.`in`.SetGoalUseCase
import ru.vachoo.notifier.domain.entities.Goal
import ru.vachoo.notifier.domain.entities.Measurement

@RestController
@RequestMapping("api/v1/goals")
class GoalsControllerV1(val setGoalUseCase: SetGoalUseCase, val getGoalsUseCase: GetGoalsUseCase) {

  @PutMapping("/{goalId}")
  fun setGoal(@PathVariable goalId: UUID, @RequestBody dto: GoalDto) {
    try {
      val userId = dto.userId ?: throw IllegalArgumentException("userId is required")
      val userToken = dto.userToken ?: throw IllegalArgumentException("userToken is required")
      val name = dto.name ?: throw IllegalArgumentException("name is required")
      val category = dto.category ?: throw IllegalArgumentException("category is required")

      val goal =
        Goal().apply {
          this.userId = userId
          this.userToken = userToken
          this.name = name
          this.category = category
          this.icon = dto.icon ?: ""
          this.color = dto.color ?: "#000000"
          this.measurement =
            Measurement().apply {
              this.currentValue = dto.measurement?.currentValue ?: 0.0
              this.targetValue = dto.measurement?.targetValue ?: 100.0
              this.unit = dto.measurement?.unit ?: ""
            }
          this.isDeleted = dto.isDeleted ?: false
        }
      setGoalUseCase.set(goalId, goal)
    } catch (e: UnauthorizedException) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
    }
  }

  @GetMapping
  fun getGoals(@RequestParam userId: UUID, @RequestParam userToken: String): List<GoalDto> {
    try {
      val goals = getGoalsUseCase.getGoals(userId, userToken)
      return goals.map { goal ->
        GoalDto(
          id = goal.id,
          userId = goal.userId,
          userToken = null,
          name = goal.name,
          category = goal.category,
          icon = goal.icon,
          color = goal.color,
          measurement =
            MeasurementDto(
              currentValue = goal.measurement.currentValue,
              targetValue = goal.measurement.targetValue,
              unit = goal.measurement.unit,
            ),
          isDeleted = goal.isDeleted,
        )
      }
    } catch (e: UnauthorizedException) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
    }
  }
}
