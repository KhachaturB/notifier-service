package ru.vachoo.notifier.adapter.out.db.notificationpreferences

import java.time.LocalTime
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

    val existingPref = findByUserIdSingle(userId)
    if (existingPref != null) {
      dslContext
        .update(NOTIFICATION_PREFERENCES)
        .set(NOTIFICATION_PREFERENCES.START_DAY_TIME, preference.startDayTime)
        .set(NOTIFICATION_PREFERENCES.END_DAY_TIME, preference.endDayTime)
        .set(NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY, preference.notificationsPerDay)
        .set(NOTIFICATION_PREFERENCES.TIMEZONE, preference.timezone)
        .where(NOTIFICATION_PREFERENCES.USER_ID.eq(userId))
        .execute()
      preference.id = existingPref.id
    } else {
      preference.id = UUID.randomUUID()
      dslContext
        .insertInto(NOTIFICATION_PREFERENCES)
        .columns(
          NOTIFICATION_PREFERENCES.ID,
          NOTIFICATION_PREFERENCES.USER_ID,
          NOTIFICATION_PREFERENCES.START_DAY_TIME,
          NOTIFICATION_PREFERENCES.END_DAY_TIME,
          NOTIFICATION_PREFERENCES.NOTIFICATIONS_PER_DAY,
          NOTIFICATION_PREFERENCES.TIMEZONE,
        )
        .values(
          preference.id,
          preference.userId,
          preference.startDayTime,
          preference.endDayTime,
          preference.notificationsPerDay,
          preference.timezone,
        )
        .execute()
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
      }
    }
}
