package ru.vachoo.notifier.adapter.out.db.quizresults

import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.generated.tables.references.QUIZ_RESULTS
import ru.vachoo.notifier.application.usecases.quizresults.out.QuizResultsDbPort
import ru.vachoo.notifier.domain.entities.QuizResult

@Component
class QuizResultsDbService(private val dslContext: DSLContext) : QuizResultsDbPort {

  override fun saveQuizResult(quizResult: QuizResult) {
    val userId = quizResult.userId ?: throw IllegalArgumentException("userId is required")
val answersArray: Array<Int?>? = quizResult.answers?.toTypedArray()

        dslContext
            .insertInto(QUIZ_RESULTS)
            .columns(
                QUIZ_RESULTS.USER_ID,
                QUIZ_RESULTS.ANSWERS,
                QUIZ_RESULTS.PRIMARY_GOAL,
                QUIZ_RESULTS.MOTIVATION_STYLE,
            )
            .values(userId, answersArray, quizResult.primaryGoal, quizResult.motivationStyle)
            .onConflict(QUIZ_RESULTS.USER_ID)
            .doUpdate()
            .set(QUIZ_RESULTS.ANSWERS, answersArray)
      .set(QUIZ_RESULTS.PRIMARY_GOAL, quizResult.primaryGoal)
      .set(QUIZ_RESULTS.MOTIVATION_STYLE, quizResult.motivationStyle)
      .execute()
  }

  override fun findByUserId(userId: UUID): QuizResult? {
    val record =
      dslContext.selectFrom(QUIZ_RESULTS).where(QUIZ_RESULTS.USER_ID.eq(userId)).fetchOne()
        ?: return null

    return QuizResult().apply {
      this.userId = record.get(QUIZ_RESULTS.USER_ID)
      this.answers = record.get(QUIZ_RESULTS.ANSWERS)?.map { it ?: 0 }
      this.primaryGoal = record.get(QUIZ_RESULTS.PRIMARY_GOAL) ?: ""
      this.motivationStyle = record.get(QUIZ_RESULTS.MOTIVATION_STYLE) ?: ""
    }
  }
}
