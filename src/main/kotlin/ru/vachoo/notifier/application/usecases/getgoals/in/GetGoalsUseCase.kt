package ru.vachoo.notifier.application.usecases.getgoals.`in`

import java.util.UUID
import ru.vachoo.notifier.domain.entities.Goal

interface GetGoalsUseCase {
  fun getGoals(userId: UUID, userToken: String): List<Goal>
}
