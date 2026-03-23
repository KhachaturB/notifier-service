package ru.vachoo.notifier.adapter.out.db.schedulednotification

import java.time.OffsetDateTime
import java.util.UUID
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import ru.vachoo.notifier.adapter.out.db.generated.tables.references.SCHEDULED_NOTIFICATIONS
import ru.vachoo.notifier.application.commonports.out.ScheduledNotificationDbPort
import ru.vachoo.notifier.domain.entities.ScheduledNotification
import ru.vachoo.notifier.domain.enums.NotificationStatus

@Component
class ScheduledNotificationDbService(val dslContext: DSLContext) : ScheduledNotificationDbPort {

  override fun save(scheduledNotification: ScheduledNotification) {
    dslContext
      .insertInto(
        org.jooq.impl.DSL.table("scheduled_notifications"),
        org.jooq.impl.DSL.field("id"),
        org.jooq.impl.DSL.field("user_id"),
        org.jooq.impl.DSL.field("status"),
        org.jooq.impl.DSL.field("message"),
        org.jooq.impl.DSL.field("scheduled_at"),
        org.jooq.impl.DSL.field("retry_count"),
        org.jooq.impl.DSL.field("next_retry_at"),
        org.jooq.impl.DSL.field("created_at"),
        org.jooq.impl.DSL.field("updated_at"),
        org.jooq.impl.DSL.field("sent_at"),
      )
      .values(
        scheduledNotification.id,
        scheduledNotification.userId,
        scheduledNotification.status.name,
        scheduledNotification.message,
        scheduledNotification.scheduledAt,
        scheduledNotification.retryCount,
        scheduledNotification.nextRetryAt,
        scheduledNotification.createdAt,
        scheduledNotification.updatedAt,
        scheduledNotification.sentAt,
      )
      .onConflict(org.jooq.impl.DSL.field("id"))
      .doUpdate()
      .set(org.jooq.impl.DSL.field("user_id"), scheduledNotification.userId)
      .set(org.jooq.impl.DSL.field("status"), scheduledNotification.status.name)
      .set(org.jooq.impl.DSL.field("message"), scheduledNotification.message)
      .set(org.jooq.impl.DSL.field("scheduled_at"), scheduledNotification.scheduledAt)
      .set(org.jooq.impl.DSL.field("retry_count"), scheduledNotification.retryCount)
      .set(org.jooq.impl.DSL.field("next_retry_at"), scheduledNotification.nextRetryAt)
      .set(
        org.jooq.impl.DSL.field("updated_at"),
        java.time.LocalDateTime.now(java.time.ZoneOffset.UTC),
      )
      .set(org.jooq.impl.DSL.field("sent_at"), scheduledNotification.sentAt)
      .execute()
  }

  override fun findPendingForProcessing(limit: Int): List<ScheduledNotification> {
    val now = java.time.LocalDateTime.now(java.time.ZoneOffset.UTC)
    return dslContext
      .select(
        org.jooq.impl.DSL.field("id"),
        org.jooq.impl.DSL.field("user_id"),
        org.jooq.impl.DSL.field("status"),
        org.jooq.impl.DSL.field("message"),
        org.jooq.impl.DSL.field("scheduled_at"),
        org.jooq.impl.DSL.field("retry_count"),
        org.jooq.impl.DSL.field("next_retry_at"),
        org.jooq.impl.DSL.field("created_at"),
        org.jooq.impl.DSL.field("updated_at"),
        org.jooq.impl.DSL.field("sent_at"),
      )
      .from(org.jooq.impl.DSL.table("scheduled_notifications"))
      .where(
        org.jooq.impl.DSL.field("status")
          .eq(NotificationStatus.PENDING.name)
          .or(org.jooq.impl.DSL.field("status").eq(NotificationStatus.FAILED.name))
      )
      .and(
        org.jooq.impl.DSL.field("scheduled_at")
          .le(now)
          .or(org.jooq.impl.DSL.field("next_retry_at").le(now))
      )
      .limit(limit)
      .fetch()
      .map { record ->
        ScheduledNotification().apply {
          this.id = record.get(org.jooq.impl.DSL.field("id"), UUID::class.java)
          this.userId = record.get(org.jooq.impl.DSL.field("user_id"), UUID::class.java)
          this.status =
            try {
              NotificationStatus.valueOf(
                record.get(DSL.field("status"), String::class.java)
                  ?: NotificationStatus.PENDING.name
              )
            } catch (e: Exception) {
              NotificationStatus.PENDING
            }
          this.message = record.get(org.jooq.impl.DSL.field("message"), String::class.java) ?: ""
          this.scheduledAt =
            record.get(org.jooq.impl.DSL.field("scheduled_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(java.time.ZoneOffset.UTC)
          this.retryCount =
            record.get(org.jooq.impl.DSL.field("retry_count"), Int::class.javaObjectType) ?: 0
          this.nextRetryAt =
            record.get(org.jooq.impl.DSL.field("next_retry_at"), OffsetDateTime::class.java)
          this.createdAt =
            record.get(org.jooq.impl.DSL.field("created_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(java.time.ZoneOffset.UTC)
          this.updatedAt =
            record.get(org.jooq.impl.DSL.field("updated_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(java.time.ZoneOffset.UTC)
          this.sentAt = record.get(org.jooq.impl.DSL.field("sent_at"), OffsetDateTime::class.java)
        }
      }
  }

  override fun existsByUserIdAndScheduledAt(userId: UUID, scheduledAt: OffsetDateTime): Boolean {
    return (dslContext
      .selectCount()
      .from(SCHEDULED_NOTIFICATIONS)
      .where(DSL.field("user_id").eq(userId))
      .and(DSL.field("scheduled_at").eq(scheduledAt))
      .fetchOne(0, Int::class.javaObjectType) ?: 0) > 0
  }

  override fun findByUserId(userId: UUID): List<ScheduledNotification> {
    return dslContext
      .select(
        org.jooq.impl.DSL.field("id"),
        org.jooq.impl.DSL.field("user_id"),
        org.jooq.impl.DSL.field("status"),
        org.jooq.impl.DSL.field("message"),
        org.jooq.impl.DSL.field("scheduled_at"),
        org.jooq.impl.DSL.field("retry_count"),
        org.jooq.impl.DSL.field("next_retry_at"),
        org.jooq.impl.DSL.field("created_at"),
        org.jooq.impl.DSL.field("updated_at"),
        org.jooq.impl.DSL.field("sent_at"),
      )
      .from(org.jooq.impl.DSL.table("scheduled_notifications"))
      .where(org.jooq.impl.DSL.field("user_id").eq(userId))
      .fetch()
      .map { record ->
        ScheduledNotification().apply {
          this.id = record.get(org.jooq.impl.DSL.field("id"), UUID::class.java)
          this.userId = record.get(org.jooq.impl.DSL.field("user_id"), UUID::class.java)
          this.status =
            try {
              NotificationStatus.valueOf(
                record.get(DSL.field("status"), String::class.java)
                  ?: NotificationStatus.PENDING.name
              )
            } catch (e: Exception) {
              NotificationStatus.PENDING
            }
          this.message = record.get(org.jooq.impl.DSL.field("message"), String::class.java) ?: ""
          this.scheduledAt =
            record.get(org.jooq.impl.DSL.field("scheduled_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(java.time.ZoneOffset.UTC)
          this.retryCount =
            record.get(org.jooq.impl.DSL.field("retry_count"), Int::class.javaObjectType) ?: 0
          this.nextRetryAt =
            record.get(org.jooq.impl.DSL.field("next_retry_at"), OffsetDateTime::class.java)
          this.createdAt =
            record.get(org.jooq.impl.DSL.field("created_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(java.time.ZoneOffset.UTC)
          this.updatedAt =
            record.get(org.jooq.impl.DSL.field("updated_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(java.time.ZoneOffset.UTC)
          this.sentAt = record.get(org.jooq.impl.DSL.field("sent_at"), OffsetDateTime::class.java)
        }
      }
  }
}
