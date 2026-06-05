# AGENTS.md - Willow Wealth Accreditation Service

Guide for AI coding agents working on this Spring Boot REST service managing user accreditation status for investment compliance.

## 🏗️ Architecture

**Layered Pattern**: `Controller → Service → Repository` with strict separation:
- **Controllers** (`AdminAccreditationController`, `UserAccreditationController`): HTTP endpoints only, delegate all logic to service
- **Service** (`AccreditationService`): Business rules, validation, orchestration
- **Repository** (`InMemoryAccreditationRepository`): Data access via `ConcurrentHashMap<UUID, Accreditation>`

**Key Design Principle**: Repository is the ONLY data access point. Never bypass it in service methods.

## 📊 Data Model & State Machine

**Accreditation States** (enums in `util/`):
- `PENDING` → initial state after creation
- `CONFIRMED` → admin approved (auto-expires after 30 days via `ExpiryScheduler`)
- `EXPIRED` → reached by scheduler OR manual finalization
- `FAILED` → terminal state, cannot be changed

**State Transition Rules** (enforced in service):
- `PENDING` can change to any state
- `CONFIRMED` can ONLY change to `EXPIRED` (checked in `finalizeAccreditation`)
- `EXPIRED` and `FAILED` are terminal (FAILED cannot change at all)

**UUID as ID**: All accreditation requests use `UUID.randomUUID()` in Accreditation constructor.

## 🔄 Request Flow Example

```
POST /user/accreditation
├─ AdminAccreditationController.createAccreditation()
├─ Service validates: no existing PENDING for user
├─ Service creates Accreditation object with id=UUID, status=PENDING
├─ Repository.save() puts into ConcurrentHashMap
└─ Response: {"accreditation_id": "uuid-string"}
```

## 🧾 Validation Patterns

**Input Validation**:
- Use `@Valid` + `@NotBlank`/`@NotNull` on request DTOs in controller parameters
- Enum validation: catch `IllegalArgumentException` from `valueOf()` in service, throw `InvalidRequestException`
- Mime type validation happens in validation constraints on `Document` class

**Custom Validations** (in service layer):
- `repository.existsPendingForUser(userId)` - prevents duplicate PENDING
- State machine checks - only `CONFIRMED→EXPIRED` allowed from CONFIRMED state

**Exception Handling**: All business exceptions throw `InvalidRequestException`, caught by `GlobalExceptionHandler` which returns `{error: "...", status: "BAD_REQUEST"}`.

## 🌍 API Conventions

**JSON Response Fields**: Use **snake_case** (e.g., `accreditation_id`, `user_id`, `accreditation_type`)

**Endpoints**:
- Admin: `POST /user/accreditation` (create), `PUT /user/accreditation/{id}` (finalize)
- Client: `GET /user/{userId}/accreditation` (list)

**Error Response Format**: 
```json
{"error": "message", "status": "BAD_REQUEST"}
```

## 🔀 Threading & Concurrency

**Thread-Safe by Design**:
- `ConcurrentHashMap<UUID, Accreditation>` in repository ensures safe concurrent access
- No synchronized blocks; relying on ConcurrentHashMap's internal locking
- `LocalDateTime` stored in models (immutable at operation level)
- Each `createAccreditation()` creates a new UUID, preventing collisions

**Scheduler Thread**: `ExpiryScheduler` runs on separate Spring `@Scheduled` thread pool, safe because updates go through repository.

## ⏱️ Background Scheduler Pattern

**ExpiryScheduler** (`@Component` with `@Scheduled`):
- Runs every `${scheduler.expiry.fixed-rate}` ms (default: 1 hour)
- Finds all CONFIRMED accreditations older than `${scheduler.expiry.days}` (default: 30)
- Updates status to EXPIRED and saves back to repository
- Logs count to stdout on matches; no failures if empty

**Config Properties** (in `application.properties`):
```properties
scheduler.expiry.fixed-rate=3600000     # 1 hour in milliseconds
scheduler.expiry.days=30                 # expiry threshold
```

When modifying scheduler logic, ALWAYS go through repository.save(), never update object in-place.

## 🧪 Testing Patterns

**Unit Tests** (service layer with Mockito):
```java
@ExtendWith(MockitoExtension.class)
class AccreditationServiceTest {
    @Mock InMemoryAccreditationRepository repository;
    @InjectMocks AccreditationService service;
    
    // Test business rules, mock repository
    when(repository.existsPendingForUser(userId)).thenReturn(true);
    assertThrows(InvalidRequestException.class, ...);
}
```

**Controller Tests** (MockMvc with @WebMvcTest):
```java
@WebMvcTest(AdminAccreditationController.class)
class AdminAccreditationControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean AccreditationService service;
    
    mockMvc.perform(post("/user/accreditation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
}
```

**Integration Tests**: Full Spring Boot context, test real flow through all layers.

All tests use `JUnit 5` with `@Test` annotations. No parameterized tests currently; add `@ParameterizedTest` with `@CsvSource` if expanding.

## 🛠️ Build & Run Commands

```bash
# Clean build
mvn clean install

# Run service (starts on http://localhost:8080)
mvn spring-boot:run

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AccreditationServiceTest

# Run integration tests specifically
mvn test -Dtest=AccreditationIntegrationTest

# Build jar (creates target/willow-wealth-accreditation-0.0.1-SNAPSHOT.jar)
mvn package
```

**Java Version**: Must be Java 17+ (defined in `pom.xml`).

## 📝 Key Files Quick Reference

| File | Purpose |
|------|---------|
| `AccreditationService.java` | Core business logic: validate, create, finalize |
| `InMemoryAccreditationRepository.java` | Data access: `ConcurrentHashMap` operations |
| `ExpiryScheduler.java` | Background task: auto-expire old confirmations |
| `GlobalExceptionHandler.java` | Centralized error handling; maps exceptions to HTTP responses |
| `Accreditation.java` | Entity with auto-UUID + timestamp generation |
| `util/AccreditationStatus.java` `util/AccreditationType.java` | State enums |

## 🚀 Common Workflows

**Add a new business rule**:
1. Define validation logic in `AccreditationService` method
2. Throw `InvalidRequestException` with clear message
3. Handler catches it automatically → 400 response

**Modify scheduler behavior**:
1. Edit `ExpiryScheduler.expireOldConfirmedAccreditations()` method
2. Always call `repository.save()` after state changes
3. Update `application.properties` defaults if needed
4. Test with `ExpirySchedulerTest`

**Add a new endpoint**:
1. Add method to appropriate controller (`AdminAccreditationController` or `UserAccreditationController`)
2. Use `@PostMapping/@GetMapping/@PutMapping`, add `@Valid` for DTOs
3. Call service method, return `ResponseEntity<Map<String, String>>` or DTO
4. Add `@Operation` Swagger annotation
5. Create MockMvc test in controller test file

**Extend repository queries**:
1. Add new method to `InMemoryAccreditationRepository` using stream API over `storage.values()`
2. Document filtering logic in method e.g. `findByStatusAndLastUpdatedBefore()`
3. Unit test with direct HashMap population in tests

## ⚙️ Configuration Customization

Override in `application.properties`:
- `server.port=8080` - REST service port
- `logging.level.com.willow.accreditation=DEBUG` - package log level
- `scheduler.expiry.fixed-rate=3600000` - scheduler interval (milliseconds)
- `scheduler.expiry.days=30` - accreditation validity period (days)
- `spring.task.scheduling.pool.size=2` - scheduler thread pool size

Swagger UI accessible at `/swagger-ui.html` when running.

## ⚠️ Critical Constraints

- **No persistent DB**: Data is in-memory only; lost on restart
- **Single service instance**: No distributed scheduler coordination
- **Document handling**: Only stores base64-encoded content; no encryption/compression
- **UUID immutability**: IDs never change; assigned once at object creation
- **State machine enforcement**: Business rules in service layer are the ONLY validation of state transitions

When writing new features, ensure they respect these constraints.

