# Agent Guidelines for Notifier Service

This document provides guidelines for agents working on the notifier-service codebase.

## Project Overview

- **Language**: Kotlin 1.9.23
- **Framework**: Spring Boot 3.5.10
- **Build Tool**: Gradle with Kotlin DSL
- **Architecture**: Hexagonal (Ports & Adapters)
- **Database**: PostgreSQL with JOOQ

## Build & Test Commands

### Standard Commands
```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run tests with verbose output
./gradlew test --info

# Clean and build
./gradlew clean build

# Run the application
./gradlew bootRun
```

### Single Test Commands
```bash
# Run a specific test class
./gradlew test --tests "com.example.MyTestClass"

# Run a specific test method
./gradlew test --tests "com.example.MyTestClass.myTestMethod"

# Run tests matching a pattern
./gradlew test --tests "*MyTest*"

# Run tests with filters
./gradlew test --tests "*Test" --tests "!*IntegrationTest"
```

### Code Quality
```bash
# Check code formatting (Spotless)
./gradlew spotlessCheck

# Apply code formatting automatically
./gradlew spotlessApply

# Run all checks (build + spotless)
./gradlew check
```

### Database Code Generation
```bash
# Generate JOOQ classes from database schema
./gradlew jooq-codegen
```

## Code Style Guidelines

### Formatting (ktfmt Google Style)
- Uses **2-space indentation** (not tabs)
- Maximum line length: 100 characters
- Trailing commas enabled
- Run `./gradlew spotlessApply` before committing
- **IMPORTANT**: Preserve existing indentation when editing code - match the indentation style of surrounding code

### Import Organization
Group imports in this order with blank lines between groups:
```kotlin
// 1. Kotlin standard library
import kotlin.io.println
import java.util.UUID

// 2. Third-party imports (Spring, JOOQ, etc.)
import org.springframework.stereotype.Component
import org.jooq.DSLContext

// 3. Project imports
import ru.vachoo.notifier.domain.entities.Reminder
```

Use backticks for reserved words in package paths:
```kotlin
import ru.vachoo.notifier.application.usecases.setreminder.`in`.SetReminderUseCase
```

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Packages | lowercase, dot-separated | `ru.vachoo.notifier.adapter.out.db` |
| Classes | PascalCase | `ReminderDbService`, `ReminderDto` |
| Interfaces | PascalCase (port/use case naming) | `ReminderDbPort`, `SetReminderUseCase` |
| Functions | camelCase | `saveReminder()`, `getRemindersByUser()` |
| Variables | camelCase | `reminderId`, `userId` |
| Constants | SCREAMING_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Test classes | Test suffix | `ReminderServiceTest` |
| Test methods | descriptive, camelCase | `shouldSaveReminder_WhenValidInput()` |

### Package Structure (Hexagonal Architecture)
```
ru.vachoo.notifier/
├── adapter/
│   ├── in/
│   │   └── web/           # REST controllers, DTOs
│   ├── out/
│   │   ├── db/            # Database adapters (JOOQ)
│   │   │   ├── user/      # UserDbService
│   │   │   └── notificationpreference/
│   │   └── quartz/        # Scheduler adapters
│   └── config/            # Configuration classes
├── application/
│   ├── services/          # Application services (TokenValidationService)
│   ├── commonports/out/   # Shared port interfaces (UserDbPort)
│   └── usecases/
│       ├── setreminder/
│       │   ├── in/        # Use case interface (SetReminderUseCase)
│       │   └── out/       # Output port interfaces
│       ├── getreminders/
│       │   ├── in/
│       │   └── out/
│       ├── setnotificationpreference/
│       │   ├── in/        # SetNotificationPreferenceUseCase
│       │   └── out/       # NotificationPreferenceDbPort
│       └── getnotificationpreferences/
│           ├── in/        # GetNotificationPreferencesUseCase
│           └── out/
├── domain/
│   └── entities/          # Domain models (Reminder, User, NotificationPreference)
├── common/
│   └── exceptions/        # Shared exceptions (UnauthorizedException)
└── NotifierService.kt     # Main application class
```

### Class Definitions

**Entities (Domain Layer)**
```kotlin
class Reminder {
  var id: UUID? = null
  var schedule: String = ""
  var userId: UUID? = null
  var text: String = ""
}
```

**Use Case Interfaces**
```kotlin
interface SetReminderUseCase {
  fun set(reminder: Reminder)
}
```

**Use Case Implementations**
```kotlin
@Component
class SetReminderUseCaseImpl(
  val reminderDbPort: ReminderDbPort,
  val schedulerPort: SchedulerPort
) : SetReminderUseCase {
  @Transactional
  override fun set(reminder: Reminder) {
    reminderDbPort.saveReminder(reminder)
    schedulerPort.schedule(reminder)
  }
}
```

**REST Controllers**
```kotlin
@RestController
@RequestMapping("api/v1/reminders")
class RemindersControllerV1(
  val modelMapper: ModelMapper,
  val setReminderUseCase: SetReminderUseCase,
  val getRemindersUseCase: GetRemindersUseCase
) {
  @PutMapping("/{id}")
  fun setNewReminder(
    @PathVariable id: UUID,
    @RequestBody reminderDto: ReminderDto
  ) {
    val reminder = modelMapper.map(reminderDto, Reminder::class.java)
    setReminderUseCase.set(reminder)
  }
}
```

**Ports (Interfaces)**
```kotlin
interface ReminderDbPort {
  fun saveReminder(reminder: Reminder)
  fun getRemindersByUser(userId: UUID): List<Reminder>
}

interface SchedulerPort {
  fun schedule(reminder: Reminder)
}
```

### Annotations Usage
- `@Component` - Spring-managed beans (services, repositories)
- `@RestController` - REST endpoints
- `@Configuration` - Configuration classes
- `@Transactional` - Database operations requiring transactions
- `@Bean` - Factory methods for Spring beans
- `@SpringBootApplication` - Main application class

### Dependency Injection
- **Constructor injection** via constructor parameters (preferred)
- Use `val` for injected dependencies
- Single-constructor classes can omit `constructor` keyword

```kotlin
// Preferred style
@Component
class MyService(val repository: MyRepository, val mapper: ModelMapper)

// Alternative (same behavior)
@Component
class MyService @Autowired constructor(
  val repository: MyRepository
)
```

### Error Handling
- Use Spring's exception handling (`@ControllerAdvice`)
- Throw domain-specific exceptions from `application/common/exceptions/`
- Return appropriate HTTP status codes in controllers
- Log errors appropriately (avoid logging sensitive data)
- Use `UnauthorizedException` for authentication failures

### Thread Safety & Concurrency
- Use `kotlinx.coroutines` for async operations when needed
- Avoid shared mutable state in services
- Use `val` for immutable fields

### Testing Guidelines
- Place tests in `src/test/kotlin/` mirroring main structure
- Use JUnit 5 with `@Test` annotation
- Use descriptive test names: `shouldSaveReminder_WhenValidInput_ThenReturnsSuccess`
- Test use cases in isolation with mocked ports
- Follow Arrange-Act-Assert pattern

### Generated Code
- JOOQ generated code lives in `adapter/out/db/generated/`
- These files are **excluded from spotless formatting** (see `build.gradle.kts`)
- Do not modify generated files directly
- Re-run `./gradlew jooq-codegen` after schema changes

### Database Schema
Liquibase migrations are stored in `src/main/resources/db/changelog/`:
- `init/` - Initial table creation (reminders)
- `features/` - Feature-specific migrations (001-users-table.yaml, 002-notification-preferences-table.yaml, etc.)

**Tables:**
- `users` - User accounts with token authentication
- `notification_preferences` - User notification settings
- `reminders` - Scheduled reminders

### API Endpoints

**Notification Preferences:**
| Method | URL | Description |
|--------|-----|-------------|
| PUT | `/api/v1/notification-preferences/{preferenceId}` | Create/update preferences |
| GET | `/api/v1/notification-preferences?userId={}&userToken={}` | Get preferences by user |

**Reminders:**
| Method | URL | Description |
|--------|-----|-------------|
| PUT | `/api/v1/reminders/{id}` | Create/update reminder |
| GET | `/api/v1/reminders?userId={}` | Get reminders by user |

### Excluded Files
The following paths are excluded from formatting:
- `**/generated/**/*.*` - JOOQ generated files

### Performance Considerations
- Use projection queries when only specific fields are needed
- Avoid N+1 queries (use `fetchInto()` appropriately)
- Consider lazy loading for large collections
