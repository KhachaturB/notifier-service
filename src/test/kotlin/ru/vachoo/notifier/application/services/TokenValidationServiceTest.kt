package ru.vachoo.notifier.application.services

import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.vachoo.notifier.application.commonports.out.UserDbPort
import ru.vachoo.notifier.domain.entities.User

@ExtendWith(MockitoExtension::class)
class TokenValidationServiceTest {

  @Mock private lateinit var userDbPort: UserDbPort

  private lateinit var service: TokenValidationService

  private val userId = UUID.randomUUID()
  private val userToken = "test-token-123"

  @BeforeEach
  fun setUp() {
    service = TokenValidationService(userDbPort)
  }

  @Test
  fun shouldReturnTrueAndCreateUser_WhenUserDoesNotExist() {
    whenever(userDbPort.findById(userId)).thenReturn(null)

    val result = service.validateOrCreateUser(userId, userToken)

    assertThat(result).isTrue()
    verify(userDbPort)
      .saveUser(
        org.mockito.kotlin.check<User> { user ->
          assertThat(user.id).isEqualTo(userId)
          assertThat(user.userToken).isEqualTo(userToken)
        }
      )
  }

  @Test
  fun shouldReturnTrue_WhenUserExistsAndTokenMatches() {
    val existingUser = spy(User())
    existingUser.id = userId
    existingUser.userToken = userToken
    whenever(userDbPort.findById(userId)).thenReturn(existingUser)

    val result = service.validateOrCreateUser(userId, userToken)

    assertThat(result).isTrue()
    verify(userDbPort, never()).saveUser(org.mockito.kotlin.any())
  }

  @Test
  fun shouldReturnFalse_WhenUserExistsAndTokenDoesNotMatch() {
    val existingUser = spy(User())
    existingUser.id = userId
    existingUser.userToken = "different-token"
    whenever(userDbPort.findById(userId)).thenReturn(existingUser)

    val result = service.validateOrCreateUser(userId, userToken)

    assertThat(result).isFalse()
    verify(userDbPort, never()).saveUser(org.mockito.kotlin.any())
  }
}
