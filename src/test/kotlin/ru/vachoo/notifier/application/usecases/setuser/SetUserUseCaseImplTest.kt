package ru.vachoo.notifier.application.usecases.setuser

import java.util.UUID
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.domain.entities.User

@ExtendWith(MockitoExtension::class)
class SetUserUseCaseImplTest {

  @Mock private lateinit var userDbPort: UserDbPort

  private lateinit var useCase: SetUserUseCaseImpl

  private val userId = UUID.randomUUID()

  @BeforeEach
  fun setUp() {
    useCase = SetUserUseCaseImpl(userDbPort)
  }

  @Test
  fun shouldCreateUser_WhenUserDoesNotExist() {
    val user =
      User().apply {
        this.id = userId
        this.userToken = "token123"
        this.username = "john"
        this.apnsToken = "apns-token"
      }
    whenever(userDbPort.findUserTokenById(userId)).thenReturn(null)

    useCase.set(user)
  }

  @Test
  fun shouldUpdateUser_WhenTokenMatches() {
    val user =
      User().apply {
        this.id = userId
        this.userToken = "token123"
        this.username = "john"
        this.apnsToken = "apns-token"
      }
    whenever(userDbPort.findUserTokenById(userId)).thenReturn("token123")

    useCase.set(user)
  }

  @Test
  fun shouldThrowForbiddenException_WhenTokenDoesNotMatch() {
    val user =
      User().apply {
        this.id = userId
        this.userToken = "wrong-token"
        this.username = "john"
        this.apnsToken = "apns-token"
      }
    whenever(userDbPort.findUserTokenById(userId)).thenReturn("token123")

    assertThatThrownBy { useCase.set(user) }
      .isInstanceOf(ForbiddenException::class.java)
      .hasMessage("Invalid user token")
  }

  @Test
  fun shouldThrowIllegalArgumentException_WhenUserIdIsNull() {
    val user =
      User().apply {
        this.id = null
        this.userToken = "token123"
        this.username = "john"
      }

    assertThatThrownBy { useCase.set(user) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("userId is required")
  }
}
