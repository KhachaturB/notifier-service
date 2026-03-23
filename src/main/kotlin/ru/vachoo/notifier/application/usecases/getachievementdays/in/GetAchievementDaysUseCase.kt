package ru.vachoo.notifier.application.usecases.getachievementdays.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.AchievementDay

interface GetAchievementDaysUseCase {
  fun getAchievementDays(userId: UUID, userToken: String): List<AchievementDay>
}
