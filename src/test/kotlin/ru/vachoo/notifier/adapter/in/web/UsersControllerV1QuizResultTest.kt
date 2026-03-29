package ru.vachoo.notifier.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.modelmapper.ModelMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import ru.vachoo.notifier.adapter.`in`.web.dtos.QuizResultDto
import ru.vachoo.notifier.application.usecases.getuser.`in`.GetUserUseCase
import ru.vachoo.notifier.application.usecases.quizresults.`in`.GetQuizResultUseCase
import ru.vachoo.notifier.application.usecases.quizresults.`in`.SaveQuizResultUseCase
import ru.vachoo.notifier.application.usecases.setuser.`in`.SetUserUseCase
import ru.vachoo.notifier.domain.entities.QuizResult

@ExtendWith(MockitoExtension::class)
class UsersControllerV1QuizResultTest {

  @Mock private lateinit var setUserUseCase: SetUserUseCase

  @Mock private lateinit var getUserUseCase: GetUserUseCase

  @Mock private lateinit var saveQuizResultUseCase: SaveQuizResultUseCase

  @Mock private lateinit var getQuizResultUseCase: GetQuizResultUseCase

  private lateinit var mockMvc: MockMvc

  private val objectMapper: ObjectMapper = jacksonObjectMapper()

  private val userId = UUID.randomUUID()

  @BeforeEach
  fun setUp() {
    val modelMapper = ModelMapper()
    val controller =
      UsersControllerV1(
        modelMapper,
        setUserUseCase,
        getUserUseCase,
        saveQuizResultUseCase,
        getQuizResultUseCase,
      )
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
  }

  @Test
  fun shouldSaveQuizResult_WhenValidRequest() {
    val dto =
      QuizResultDto(
        answers = listOf(1, 2, 3),
        primaryGoal = "Lose weight",
        motivationStyle = "achievement",
      )

    mockMvc
      .perform(
        post("/api/v1/users/{userId}/quiz-result", userId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(dto))
      )
      .andExpect(status().isOk())
  }

  @Test
  fun shouldGetQuizResult_WhenFound() {
    val quizResult = QuizResult()
    quizResult.userId = userId
    quizResult.answers = listOf(1, 2, 3)
    quizResult.primaryGoal = "Lose weight"
    quizResult.motivationStyle = "achievement"

    whenever(getQuizResultUseCase.get(userId)).thenReturn(quizResult)

    mockMvc
      .perform(get("/api/v1/users/{userId}/quiz-result", userId))
      .andExpect(status().isOk())
.andExpect(jsonPath("$.answers[0]").value(1))
            .andExpect(jsonPath("$.answers[1]").value(2))
            .andExpect(jsonPath("$.answers[2]").value(3))
      .andExpect(jsonPath("$.primaryGoal").value("Lose weight"))
      .andExpect(jsonPath("$.motivationStyle").value("achievement"))
  }

  @Test
  fun shouldReturn404_WhenQuizResultNotFound() {
    whenever(getQuizResultUseCase.get(userId)).thenReturn(null)

    mockMvc
      .perform(get("/api/v1/users/{userId}/quiz-result", userId))
      .andExpect(status().isNotFound())
  }
}
