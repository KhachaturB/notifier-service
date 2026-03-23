package ru.vachoo.notifier.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalTime
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.modelmapper.ModelMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import ru.vachoo.notifier.adapter.`in`.web.dtos.NotificationPreferenceDto
import ru.vachoo.notifier.application.exceptions.UnauthorizedException
import ru.vachoo.notifier.application.usecases.getnotificationpreferences.`in`.GetNotificationPreferencesUseCase
import ru.vachoo.notifier.application.usecases.setnotificationpreference.`in`.SetNotificationPreferenceUseCase
import ru.vachoo.notifier.domain.entities.NotificationPreference

@ExtendWith(MockitoExtension::class)
class NotificationPreferencesControllerV1Test {

  @Mock private lateinit var setNotificationPreferenceUseCase: SetNotificationPreferenceUseCase

  @Mock private lateinit var getNotificationPreferencesUseCase: GetNotificationPreferencesUseCase

  private lateinit var mockMvc: MockMvc

  private val objectMapper: ObjectMapper =
    jacksonObjectMapper().apply { registerModule(JavaTimeModule()) }

  private val userId = UUID.randomUUID()
  private val preferenceId = UUID.randomUUID()
  private val userToken = "test-token"

  @BeforeEach
  fun setUp() {
    val modelMapper = ModelMapper()
    val controller =
      NotificationPreferencesControllerV1(
        modelMapper,
        setNotificationPreferenceUseCase,
        getNotificationPreferencesUseCase,
      )
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
  }

  @Test
  fun shouldSetPreference_WhenValidRequest() {
    val dto =
      NotificationPreferenceDto(
        userId = userId,
        userToken = userToken,
        startDayTime = LocalTime.of(9, 0),
        endDayTime = LocalTime.of(21, 0),
        notificationsPerDay = 5,
      )

    mockMvc
      .perform(
        put("/api/v1/notification-preferences/{preferenceId}", preferenceId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(dto))
      )
      .andExpect(status().isOk())

    verify(setNotificationPreferenceUseCase).set(any(), any())
  }

  @Test
  fun shouldGetPreferences_WhenValidRequest() {
    val preference =
      NotificationPreference().apply {
        this.id = preferenceId
        this.userId = userId
        this.startDayTime = LocalTime.of(9, 0)
        this.endDayTime = LocalTime.of(21, 0)
        this.notificationsPerDay = 5
      }
    whenever(getNotificationPreferencesUseCase.getPreferences(userId, userToken))
      .thenReturn(listOf(preference))

    mockMvc
      .perform(
        get("/api/v1/notification-preferences")
          .param("userId", userId.toString())
          .param("userToken", userToken)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(preferenceId.toString()))
      .andExpect(jsonPath("$[0].startDayTime").value("09:00"))
      .andExpect(jsonPath("$[0].endDayTime").value("21:00"))
      .andExpect(jsonPath("$[0].notificationsPerDay").value(5))
  }

  @Test
  fun shouldReturn401_WhenUnauthorizedOnGet() {
    whenever(getNotificationPreferencesUseCase.getPreferences(userId, userToken))
      .thenThrow(UnauthorizedException("Invalid user token"))

    mockMvc
      .perform(
        get("/api/v1/notification-preferences")
          .param("userId", userId.toString())
          .param("userToken", userToken)
      )
      .andExpect(status().isUnauthorized)
  }

  @Test
  fun shouldReturnEmptyList_WhenNoPreferencesExist() {
    whenever(getNotificationPreferencesUseCase.getPreferences(userId, userToken))
      .thenReturn(emptyList())

    mockMvc
      .perform(
        get("/api/v1/notification-preferences")
          .param("userId", userId.toString())
          .param("userToken", userToken)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray)
      .andExpect(jsonPath("$").isEmpty)
  }
}
