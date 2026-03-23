package ru.vachoo.notifier.application.usecases.setgoal.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.Goal

interface SetGoalUseCase {
  fun set(goalId: UUID, goal: Goal)
}
