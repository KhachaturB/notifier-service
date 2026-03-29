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
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val id = user.id ?: UUID.randomUUID()

    val existing =
      dslContext
        .select(USERS.USER_TOKEN, USERS.USERNAME, USERS.APNS_TOKEN)
        .from(USERS)
        .where(USERS.ID.eq(id))
        .fetchOne()

    val hasChanges =
      existing == null ||
        existing[USERS.USER_TOKEN] != user.userToken ||
        existing[USERS.USERNAME] != user.username ||
        existing[USERS.APNS_TOKEN] != user.apnsToken

    if (existing == null) {
      dslContext
        .insertInto(USERS)
        .columns(
          USERS.ID,
          USERS.USER_TOKEN,
          USERS.USERNAME,
          USERS.APNS_TOKEN,
          USERS.CREATED_AT,
          USERS.UPDATED_AT,
        )
        .values(id, user.userToken, user.username, user.apnsToken, now, now)
        .execute()
    } else if (hasChanges) {
      dslContext
        .update(USERS)
        .set(USERS.USER_TOKEN, user.userToken)
        .set(USERS.USERNAME, user.username)
        .set(USERS.APNS_TOKEN, user.apnsToken)
        .set(USERS.UPDATED_AT, now)
        .where(USERS.ID.eq(id))
        .execute()
    }

    user.id = id

    val savedTimestamps =
      dslContext
        .select(USERS.CREATED_AT, USERS.UPDATED_AT)
        .from(USERS)
        .where(USERS.ID.eq(id))
        .fetchOne()

    if (savedTimestamps != null) {
      user.createdAt = savedTimestamps[USERS.CREATED_AT] ?: now
      user.updatedAt = savedTimestamps[USERS.UPDATED_AT] ?: now
    }
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
