package ru.vachoo.notifier.adapter.`in`.web

import java.util.UUID
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.vachoo.notifier.adapter.`in`.web.dtos.NotificationPreferenceDto
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.`in`.GetNotificationPreferencesUseCase
import ru.vachoo.notifier.application.usecases.setnotificationpreference.`in`.SetNotificationPreferenceUseCase
import ru.vachoo.notifier.domain.entities.NotificationPreference

@RestController
@RequestMapping("api/v1/notification-preferences")
class NotificationPreferencesControllerV1(
  val modelMapper: ModelMapper,
  val setNotificationPreferenceUseCase: SetNotificationPreferenceUseCase,
  val getNotificationPreferencesUseCase: GetNotificationPreferencesUseCase,
) {

  @PostMapping
  fun setNotificationPreference(@RequestBody dto: NotificationPreferenceDto) {
    try {
      val preference = modelMapper.map(dto, NotificationPreference::class.java)
      setNotificationPreferenceUseCase.set(UUID.randomUUID(), preference)
    } catch (e: UnauthorizedException) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
    }
  }

  @GetMapping
  fun getNotificationPreferences(
    @RequestParam userId: UUID,
    @RequestParam userToken: String,
  ): List<NotificationPreferenceDto> {
    try {
      val preferences = getNotificationPreferencesUseCase.getPreferences(userId, userToken)
      return preferences.map { modelMapper.map(it, NotificationPreferenceDto::class.java) }
    } catch (e: UnauthorizedException) {
      throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
    }
  }
}
