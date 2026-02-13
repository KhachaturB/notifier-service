package ru.vachoo.notifier.adapter.`in`.web

import java.util.UUID
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.vachoo.notifier.adapter.`in`.web.dtos.ReminderDto
import ru.vachoo.notifier.application.usecases.getreminders.`in`.GetRemindersUseCase
import ru.vachoo.notifier.application.usecases.setreminder.`in`.SetReminderUseCase
import ru.vachoo.notifier.domain.entities.Reminder

@RestController
@RequestMapping("api/v1/reminders")
class RemindersControllerV1(
  val modelMapper: ModelMapper,
  val setReminderUseCase: SetReminderUseCase,
  val getRemindersUseCase: GetRemindersUseCase,
) {

  @PutMapping("/{id}")
  fun setNewReminder(@PathVariable id: UUID, @RequestBody reminderDto: ReminderDto) {
    val reminder = modelMapper.map(reminderDto, Reminder::class.java).apply { this.id = id }
    setReminderUseCase.set(reminder)
  }

  @GetMapping
  fun getRemindersByUser(@RequestParam userId: UUID): List<ReminderDto> {
    val reminders = getRemindersUseCase.getReminders(userId)
    return reminders.map { modelMapper.map(it, ReminderDto::class.java) }
  }
}
