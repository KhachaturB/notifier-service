package ru.vachoo.notifier.application.usecases.getgoals.out

import java.util.UUID
import ru.vachoo.notifier.domain.entities.Goal

interface GetGoalsDbPort {
  fun findByUserId(userId: UUID): List<Goal>
}
