package ru.vachoo.notifier.adapter.out.db.achievementday

import java.time.OffsetDateTime
import java.util.UUID
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import ru.vachoo.notifier.application.usecases.getachievementdays.out.GetAchievementDaysDbPort
import ru.vachoo.notifier.application.usecases.getgoals.out.SetAchievementDayDbPort
import ru.vachoo.notifier.domain.entities.AchievementDay

@Component
class AchievementDaysDbService(val dslContext: DSLContext) :
  SetAchievementDayDbPort, GetAchievementDaysDbPort {

  override fun saveAchievementDay(achievementDay: AchievementDay) {
    dslContext
      .insertInto(
        DSL.table("achievement_days"),
        DSL.field("id"),
        DSL.field("user_id"),
        DSL.field("date"),
      )
      .values(achievementDay.id, achievementDay.userId, achievementDay.date)
      .onConflict(DSL.field("id"))
      .doUpdate()
      .set(DSL.field("user_id"), achievementDay.userId)
      .set(DSL.field("date"), achievementDay.date)
      .execute()
  }

  override fun findByUserId(userId: UUID): List<AchievementDay> =
    dslContext
      .select(DSL.field("id"), DSL.field("user_id"), DSL.field("date"))
      .from(DSL.table("achievement_days"))
      .where(DSL.field("user_id").eq(userId))
      .fetch()
      .into(AchievementDay::class.java)

  override fun hasAchievementDayForDate(userId: UUID, date: OffsetDateTime): Boolean {
    val count =
      dslContext
        .selectCount()
        .from(DSL.table("achievement_days"))
        .where(DSL.field("user_id").eq(userId))
        .and(DSL.field("date").eq(date))
        .fetchOne(0, Int::class.java)
    return (count ?: 0) > 0
  }
}
