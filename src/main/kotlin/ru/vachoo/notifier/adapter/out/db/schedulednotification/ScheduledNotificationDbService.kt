package ru.vachoo.notifier.adapter.out.db.schedulednotification

import java.time.OffsetDateTime
import java.time.ZoneOffset
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

  private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

  override fun save(scheduledNotification: ScheduledNotification) {
    log.info("Saving notification: scheduledAt={}", scheduledNotification.scheduledAt)
    dslContext
      .insertInto(
        DSL.table("scheduled_notifications"),
        DSL.field("id"),
        DSL.field("user_id"),
        DSL.field("status"),
        DSL.field("message"),
        DSL.field("scheduled_at"),
        DSL.field("retry_count"),
        DSL.field("next_retry_at"),
        DSL.field("created_at"),
        DSL.field("updated_at"),
        DSL.field("sent_at"),
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
      .onConflict(DSL.field("id"))
      .doUpdate()
      .set(DSL.field("user_id"), scheduledNotification.userId)
      .set(DSL.field("status"), scheduledNotification.status.name)
      .set(DSL.field("message"), scheduledNotification.message)
      .set(DSL.field("scheduled_at"), scheduledNotification.scheduledAt)
      .set(DSL.field("retry_count"), scheduledNotification.retryCount)
      .set(DSL.field("next_retry_at"), scheduledNotification.nextRetryAt)
      .set(DSL.field("updated_at"), OffsetDateTime.now(ZoneOffset.UTC))
      .set(DSL.field("sent_at"), scheduledNotification.sentAt)
      .execute()
  }

  override fun findPendingForProcessing(limit: Int): List<ScheduledNotification> {
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    return dslContext
      .select(
        DSL.field("id"),
        DSL.field("user_id"),
        DSL.field("status"),
        DSL.field("message"),
        DSL.field("scheduled_at"),
        DSL.field("retry_count"),
        DSL.field("next_retry_at"),
        DSL.field("created_at"),
        DSL.field("updated_at"),
        DSL.field("sent_at"),
      )
      .from(DSL.table("scheduled_notifications"))
      .where(
        DSL.field("status")
          .eq(NotificationStatus.PENDING.name)
          .or(DSL.field("status").eq(NotificationStatus.FAILED.name))
      )
      .and(DSL.field("scheduled_at").le(now).or(DSL.field("next_retry_at").le(now)))
      .limit(limit)
      .fetch()
      .map { record ->
        ScheduledNotification().apply {
          this.id = record.get(DSL.field("id"), UUID::class.java)
          this.userId = record.get(DSL.field("user_id"), UUID::class.java)
          this.status =
            try {
              NotificationStatus.valueOf(
                record.get(DSL.field("status"), String::class.java)
                  ?: NotificationStatus.PENDING.name
              )
            } catch (e: Exception) {
              NotificationStatus.PENDING
            }
          this.message = record.get(DSL.field("message"), String::class.java) ?: ""
          this.scheduledAt =
            record.get(DSL.field("scheduled_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.retryCount = record.get(DSL.field("retry_count"), Int::class.javaObjectType) ?: 0
          this.nextRetryAt = record.get(DSL.field("next_retry_at"), OffsetDateTime::class.java)
          this.createdAt =
            record.get(DSL.field("created_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.updatedAt =
            record.get(DSL.field("updated_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.sentAt = record.get(DSL.field("sent_at"), OffsetDateTime::class.java)
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
        DSL.field("id"),
        DSL.field("user_id"),
        DSL.field("status"),
        DSL.field("message"),
        DSL.field("scheduled_at"),
        DSL.field("retry_count"),
        DSL.field("next_retry_at"),
        DSL.field("created_at"),
        DSL.field("updated_at"),
        DSL.field("sent_at"),
      )
      .from(DSL.table("scheduled_notifications"))
      .where(DSL.field("user_id").eq(userId))
      .fetch()
      .map { record ->
        ScheduledNotification().apply {
          this.id = record.get(DSL.field("id"), UUID::class.java)
          this.userId = record.get(DSL.field("user_id"), UUID::class.java)
          this.status =
            try {
              NotificationStatus.valueOf(
                record.get(DSL.field("status"), String::class.java)
                  ?: NotificationStatus.PENDING.name
              )
            } catch (e: Exception) {
              NotificationStatus.PENDING
            }
          this.message = record.get(DSL.field("message"), String::class.java) ?: ""
          this.scheduledAt =
            record.get(DSL.field("scheduled_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.retryCount = record.get(DSL.field("retry_count"), Int::class.javaObjectType) ?: 0
          this.nextRetryAt = record.get(DSL.field("next_retry_at"), OffsetDateTime::class.java)
          this.createdAt =
            record.get(DSL.field("created_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.updatedAt =
            record.get(DSL.field("updated_at"), OffsetDateTime::class.java)
              ?: OffsetDateTime.now(ZoneOffset.UTC)
          this.sentAt = record.get(DSL.field("sent_at"), OffsetDateTime::class.java)
        }
      }
  }

  override fun cancelPendingByUserId(userId: UUID) {
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    val updated =
      dslContext
        .update(DSL.table("scheduled_notifications"))
        .set(DSL.field("status"), NotificationStatus.CANCELLED.name)
        .set(DSL.field("updated_at"), now)
        .where(DSL.field("user_id").eq(userId))
        .and(DSL.field("status").eq(NotificationStatus.PENDING.name))
        .execute()
    log.info("Cancelled {} pending notifications for userId={}", updated, userId)
  }
}
