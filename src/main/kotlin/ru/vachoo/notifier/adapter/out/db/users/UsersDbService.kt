package ru.vachoo.notifier.adapter.out.db.users

import java.time.LocalDateTime
import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.generated.tables.references.USERS
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.domain.entities.User

@Component
class UsersDbService(val dslContext: DSLContext) : UserDbPort {

  override fun findById(userId: UUID): User? =
    dslContext.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne()?.into(User::class.java)

  override fun saveUser(user: User) {
    dslContext
      .insertInto(USERS)
      .columns(USERS.ID, USERS.USER_TOKEN, USERS.CREATED_AT)
      .values(user.id, user.userToken, LocalDateTime.now())
      .onDuplicateKeyUpdate()
      .set(USERS.USER_TOKEN, user.userToken)
      .set(USERS.UPDATED_AT, LocalDateTime.now())
      .execute()
  }

  override fun existsById(userId: UUID): Boolean =
    dslContext.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne() != null
}
