package ru.vachoo.notifier.application.usecases.setgoal.out

import ru.vachoo.notifier.domain.entities.Goal

interface SetGoalDbPort {
  fun saveGoal(goal: Goal)
}
