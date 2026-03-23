package ru.vachoo.notifier.adapter.`in`.web

import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.vachoo.notifier.adapter.`in`.web.dtos.AchievementDayDto
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.usecases.getachievementdays.`in`.GetAchievementDaysUseCase

@RestController
@RequestMapping("api/v1/achievement-days")
class AchievementDaysControllerV1(val getAchievementDaysUseCase: GetAchievementDaysUseCase) {

  @GetMapping
  fun getAchievementDays(
    @RequestParam userId: UUID,
    @RequestParam userToken: String,
  ): List<AchievementDayDto> {
    try {
      val achievementDays = getAchievementDaysUseCase.getAchievementDays(userId, userToken)
      return achievementDays.map { day ->
        AchievementDayDto(id = day.id, userId = day.userId, userToken = null, date = day.date)
      }
    } catch (e: UnauthorizedException) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
    }
  }
}
