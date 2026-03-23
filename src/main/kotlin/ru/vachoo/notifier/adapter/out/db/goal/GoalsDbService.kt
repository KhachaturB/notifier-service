package ru.vachoo.notifier.adapter.out.db.goal

import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.generated.tables.references.GOALS
import ru.vachoo.notifier.application.usecases.getgoals.out.GetGoalsDbPort
import ru.vachoo.notifier.application.usecases.setgoal.out.SetGoalDbPort
import ru.vachoo.notifier.domain.entities.Goal
import ru.vachoo.notifier.domain.entities.Measurement
import ru.vachoo.notifier.domain.enums.GoalCategory

@Component
class GoalsDbService(val dslContext: DSLContext) : SetGoalDbPort, GetGoalsDbPort {

  override fun saveGoal(goal: Goal) {
    dslContext
      .insertInto(GOALS)
      .columns(
        GOALS.ID,
        GOALS.USER_ID,
        GOALS.NAME,
        GOALS.CATEGORY,
        GOALS.ICON,
        GOALS.COLOR,
        GOALS.CURRENT_VALUE,
        GOALS.TARGET_VALUE,
        GOALS.UNIT,
        GOALS.IS_DELETED,
      )
      .values(
        goal.id,
        goal.userId,
        goal.name,
        goal.category.name,
        goal.icon,
        goal.color,
        goal.measurement.currentValue,
        goal.measurement.targetValue,
        goal.measurement.unit,
        goal.isDeleted,
      )
      .onConflict(GOALS.ID)
      .doUpdate()
      .set(GOALS.USER_ID, goal.userId)
      .set(GOALS.NAME, goal.name)
      .set(GOALS.CATEGORY, goal.category.name)
      .set(GOALS.ICON, goal.icon)
      .set(GOALS.COLOR, goal.color)
      .set(GOALS.CURRENT_VALUE, goal.measurement.currentValue)
      .set(GOALS.TARGET_VALUE, goal.measurement.targetValue)
      .set(GOALS.UNIT, goal.measurement.unit)
      .set(GOALS.IS_DELETED, goal.isDeleted)
      .execute()
  }

  override fun findByUserId(userId: UUID): List<Goal> =
    dslContext
      .selectFrom(GOALS)
      .where(GOALS.USER_ID.eq(userId))
      .and(GOALS.IS_DELETED.ne(true))
      .fetch()
      .map { record ->
        Goal().apply {
          this.id = record.get(GOALS.ID)
          this.userId = record.get(GOALS.USER_ID)
          this.name = record.get(GOALS.NAME) ?: ""
          this.category =
            try {
              GoalCategory.valueOf(record.get(GOALS.CATEGORY) ?: "PERSONAL")
            } catch (e: Exception) {
              GoalCategory.PERSONAL
            }
          this.icon = record.get(GOALS.ICON) ?: ""
          this.color = record.get(GOALS.COLOR) ?: "#000000"
          this.measurement =
            Measurement().apply {
              this.currentValue = record.get(GOALS.CURRENT_VALUE) ?: 0.0
              this.targetValue = record.get(GOALS.TARGET_VALUE) ?: 100.0
              this.unit = record.get(GOALS.UNIT) ?: ""
            }
          this.isDeleted = record.get(GOALS.IS_DELETED) ?: false
        }
      }
}
