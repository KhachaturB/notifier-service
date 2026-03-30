package ru.vachoo.notifier.adapter.`in`.web

import java.util.UUID
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.vachoo.notifier.adapter.`in`.web.dtos.QuizResultDto
import ru.vachoo.notifier.adapter.`in`.web.dtos.SaveUserDto
import ru.vachoo.notifier.adapter.`in`.web.dtos.UserResponseDto
import ru.vachoo.notifier.application.exceptions.ForbiddenException
import ru.vachoo.notifier.application.usecases.getuser.`in`.GetUserUseCase
import ru.vachoo.notifier.application.usecases.quizresults.`in`.GetQuizResultUseCase
import ru.vachoo.notifier.application.usecases.quizresults.`in`.SaveQuizResultUseCase
import ru.vachoo.notifier.application.usecases.setuser.`in`.SetUserUseCase
import ru.vachoo.notifier.domain.entities.QuizResult
import ru.vachoo.notifier.domain.entities.User

@RestController
@RequestMapping("api/v1/users")
class UsersControllerV1(
  val modelMapper: ModelMapper,
  val setUserUseCase: SetUserUseCase,
  val getUserUseCase: GetUserUseCase,
  val saveQuizResultUseCase: SaveQuizResultUseCase,
  val getQuizResultUseCase: GetQuizResultUseCase,
) {

  @PutMapping("/{userId}")
  fun setUser(@PathVariable userId: UUID, @RequestBody dto: SaveUserDto) {
    try {
      val user = modelMapper.map(dto, User::class.java).apply { this.id = userId }
      setUserUseCase.set(user)
    } catch (e: ForbiddenException) {
      throw ResponseStatusException(HttpStatus.FORBIDDEN, e.message)
    }
  }

  @GetMapping("/{userId}")
  fun getUser(@PathVariable userId: UUID): UserResponseDto {
    val user =
      getUserUseCase.get(userId)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    return UserResponseDto(id = user.id.toString(), username = user.username)
  }

@PostMapping("/{userId}/quiz-result")
    fun saveQuizResult(@PathVariable userId: UUID, @RequestBody dto: QuizResultDto) {
        try {
            val quizResult =
                QuizResult().apply {
                    this.userId = userId
                    this.answers = dto.answers
                    this.primaryGoal = dto.primaryGoal
                    this.motivationStyle = dto.motivationStyle
                }
            saveQuizResultUseCase.save(userId, dto.userToken, quizResult)
        } catch (e: ForbiddenException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, e.message)
        }
    }

    @GetMapping("/{userId}/quiz-result")
    fun getQuizResult(
        @PathVariable userId: UUID,
        @RequestParam userToken: String
    ): QuizResultDto {
        try {
            val quizResult =
                getQuizResultUseCase.get(userId, userToken)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz result not found")
            return QuizResultDto(
                answers = quizResult.answers,
                primaryGoal = quizResult.primaryGoal,
                motivationStyle = quizResult.motivationStyle,
            )
        } catch (e: ForbiddenException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, e.message)
        }
    }
}
