package ru.vachoo.notifier.adapter.`in`.web

import java.util.UUID
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.vachoo.notifier.adapter.`in`.web.dtos.NotificationPreferenceDto
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.`in`.GetNotificationPreferencesUseCase
import ru.vachoo.notifier.application.usecases.setnotificationpreference.`in`.SetNotificationPreferenceUseCase

@RestController
@RequestMapping("api/v1/notification-preferences")
class NotificationPreferencesControllerV1(
  val modelMapper: ModelMapper,
  val setNotificationPreferenceUseCase: SetNotificationPreferenceUseCase,
  val getNotificationPreferencesUseCase: GetNotificationPreferencesUseCase,
) {

  @PutMapping("/{preferenceId}")
  fun setNotificationPreference(
    @PathVariable preferenceId: UUID,
    @RequestBody dto: NotificationPreferenceDto,
  ) {
    try {
      setNotificationPreferenceUseCase.set(preferenceId, dto)
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
