package ru.vachoo.notifier.application.usecases.getachievementdays.out

import java.time.OffsetDateTime
import java.util.UUID

interface GetAchievementDaysDbPort {
  fun findByUserId(userId: UUID): List<ru.vachoo.notifier.domain.entities.AchievementDay>

  fun hasAchievementDayForDate(userId: UUID, date: OffsetDateTime): Boolean
}
