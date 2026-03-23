package ru.vachoo.notifier.application.usecases.getgoals.out

import ru.vachoo.notifier.domain.entities.AchievementDay

interface SetAchievementDayDbPort {
  fun saveAchievementDay(achievementDay: AchievementDay)
}
