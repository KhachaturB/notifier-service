package ru.vachoo.notifier.adapter.out.db.notificationpreferences

import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.generated.tables.references.NOTIFICATION_PREFERENCES
import ru.vachoo.notifier.application.usecases.createschedsnotifs.out.NotificationPreferencesDbPort
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.out.GetNotificationPreferencesDbPort
import ru.vachoo.notifier.application.usecases.setnotificationpreference.out.SetNotificationPreferenceDbPort
import ru.vachoo.notifier.domain.entities.NotificationPreference

@Component
class NotificationPreferencesDbService(val dslContext: DSLContext) :
  SetNotificationPreferenceDbPort, GetNotificationPreferencesDbPort, NotificationPreferencesDbPort {

  override fun saveNotificationPreference(preference: NotificationPreference) {
    val userId = preference.userId ?: return
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val id = preference.id ?: UUID.randomUUID()

    val existing =
      dslContext
        .select(
          NOTIFICATION_PREFERENCES.START_DAY_TIME,
          NOTIFICATION_PREFERENCES.END_DAY_TIME,
          NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY,
          NOTIFICATION_PREFERENCES.TIMEZONE,
        )
        .from(NOTIFICATION_PREFERENCES)
        .where(NOTIFICATION_PREFERENCES.USER_ID.eq(userId))
        .fetchOne()

    val dbStartDayTime = existing?.get(NOTIFICATION_PREFERENCES.START_DAY_TIME)
    val dbEndDayTime = existing?.get(NOTIFICATION_PREFERENCES.END_DAY_TIME)
    val dbNotificationsPerDay = existing?.get(NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY)
    val dbTimezone = existing?.get(NOTIFICATION_PREFERENCES.TIMEZONE)

    val hasChanges =
      existing == null ||
        dbStartDayTime != preference.startDayTime ||
        dbEndDayTime != preference.endDayTime ||
        dbNotificationsPerDay != preference.notificationsPerDay ||
        dbTimezone != preference.timezone

    if (existing == null) {
      dslContext
        .insertInto(NOTIFICATION_PREFERENCES)
        .columns(
          NOTIFICATION_PREFERENCES.ID,
          NOTIFICATION_PREFERENCES.USER_ID,
          NOTIFICATION_PREFERENCES.START_DAY_TIME,
          NOTIFICATION_PREFERENCES.END_DAY_TIME,
          NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY,
          NOTIFICATION_PREFERENCES.TIMEZONE,
          NOTIFICATION_PREFERENCES.CREATED_AT,
          NOTIFICATION_PREFERENCES.UPDATED_AT,
        )
        .values(
          id,
          userId,
          preference.startDayTime,
          preference.endDayTime,
          preference.notificationsPerDay,
          preference.timezone,
          now,
          now,
        )
        .execute()
    } else if (hasChanges) {
      dslContext
        .update(NOTIFICATION_PREFERENCES)
        .set(NOTIFICATION_PREFERENCES.START_DAY_TIME, preference.startDayTime)
        .set(NOTIFICATION_PREFERENCES.END_DAY_TIME, preference.endDayTime)
        .set(NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY, preference.notificationsPerDay)
        .set(NOTIFICATION_PREFERENCES.TIMEZONE, preference.timezone)
        .set(NOTIFICATION_PREFERENCES.UPDATED_AT, now)
        .where(NOTIFICATION_PREFERENCES.USER_ID.eq(userId))
        .execute()
    }

    preference.id = id

    val savedTimestamps =
      dslContext
        .select(NOTIFICATION_PREFERENCES.CREATED_AT, NOTIFICATION_PREFERENCES.UPDATED_AT)
        .from(NOTIFICATION_PREFERENCES)
        .where(NOTIFICATION_PREFERENCES.USER_ID.eq(userId))
        .fetchOne()

    if (savedTimestamps != null) {
      preference.createdAt = savedTimestamps[NOTIFICATION_PREFERENCES.CREATED_AT] ?: now
      preference.updatedAt = savedTimestamps[NOTIFICATION_PREFERENCES.UPDATED_AT] ?: now
    }
  }

  override fun findById(preferenceId: UUID): NotificationPreference? =
    dslContext
      .selectFrom(NOTIFICATION_PREFERENCES)
      .where(NOTIFICATION_PREFERENCES.ID.eq(preferenceId))
      .fetchOne()
      ?.into(NotificationPreference::class.java)

  override fun findByUserIdSingle(userId: UUID): NotificationPreference? =
    dslContext
      .selectFrom(NOTIFICATION_PREFERENCES)
      .where(NOTIFICATION_PREFERENCES.USER_ID.eq(userId))
      .fetchOne()
      ?.into(NotificationPreference::class.java)

  override fun findByUserId(userId: UUID): List<NotificationPreference> =
    dslContext
      .selectFrom(NOTIFICATION_PREFERENCES)
      .where(NOTIFICATION_PREFERENCES.USER_ID.eq(userId))
      .fetch()
      .map { record ->
        NotificationPreference().apply {
          this.id = record.get(NOTIFICATION_PREFERENCES.ID)
          this.userId = record.get(NOTIFICATION_PREFERENCES.USER_ID)
          this.startDayTime =
            record.get(NOTIFICATION_PREFERENCES.START_DAY_TIME) ?: LocalTime.of(9, 0)
          this.endDayTime = record.get(NOTIFICATION_PREFERENCES.END_DAY_TIME) ?: LocalTime.of(21, 0)
          this.notificationsPerDay = record.get(NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY) ?: 5
          this.timezone = record.get(NOTIFICATION_PREFERENCES.TIMEZONE) ?: "UTC"
          this.createdAt =
            record.get(NOTIFICATION_PREFERENCES.CREATED_AT) ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.updatedAt =
            record.get(NOTIFICATION_PREFERENCES.UPDATED_AT) ?: OffsetDateTime.now(ZoneOffset.UTC)
        }
      }

  override fun findAll(): List<NotificationPreference> =
    dslContext.selectFrom(NOTIFICATION_PREFERENCES).fetch().map { record ->
      NotificationPreference().apply {
        this.id = record.get(NOTIFICATION_PREFERENCES.ID)
        this.userId = record.get(NOTIFICATION_PREFERENCES.USER_ID)
        this.startDayTime =
          record.get(NOTIFICATION_PREFERENCES.START_DAY_TIME) ?: LocalTime.of(9, 0)
        this.endDayTime = record.get(NOTIFICATION_PREFERENCES.END_DAY_TIME) ?: LocalTime.of(21, 0)
        this.notificationsPerDay = record.get(NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY) ?: 5
        this.timezone = record.get(NOTIFICATION_PREFERENCES.TIMEZONE) ?: "UTC"
        this.createdAt =
          record.get(NOTIFICATION_PREFERENCES.CREATED_AT) ?: OffsetDateTime.now(ZoneOffset.UTC)
        this.updatedAt =
          record.get(NOTIFICATION_PREFERENCES.UPDATED_AT) ?: OffsetDateTime.now(ZoneOffset.UTC)
      }
    }
}
