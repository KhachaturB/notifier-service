package ru.vachoo.notifier.adapter.out.db.users

import java.time.OffsetDateTime
import java.time.ZoneOffset
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
      .columns(USERS.ID, USERS.USER_TOKEN, USERS.USERNAME, USERS.APNS_TOKEN, USERS.CREATED_AT)
      .values(
        user.id,
        user.userToken,
        user.username,
        user.apnsToken,
        OffsetDateTime.now(ZoneOffset.UTC),
      )
      .onConflict(USERS.ID)
      .doUpdate()
      .set(USERS.USER_TOKEN, user.userToken)
      .set(USERS.USERNAME, user.username)
      .set(USERS.APNS_TOKEN, user.apnsToken)
      .set(USERS.UPDATED_AT, OffsetDateTime.now(ZoneOffset.UTC))
      .execute()
  }

  override fun existsById(userId: UUID): Boolean =
    dslContext.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne() != null

  override fun findUserTokenById(userId: UUID): String? =
    dslContext
      .select(USERS.USER_TOKEN)
      .from(USERS)
      .where(USERS.ID.eq(userId))
      .fetchOne(USERS.USER_TOKEN)
}
