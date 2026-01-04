# Annotations & Concepts - Complete Reference

## 📚 Index

- [Spring Core](#spring-core-annotations)
- [Dependency Injection](#dependency-injection)
- [JPA/Hibernate](#jpa-hibernate)
- [Validation](#validation)
- [Lombok](#lombok)
- [Security](#security)
- [REST API](#rest-api)
- [Async & Scheduling](#async--scheduling)
- [Transactions](#transactions)

---

## Spring Core Annotations

### @Configuration

**Purpose:** Marks a class as a configuration class that defines Spring beans.

```java
@Configuration
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**How it works:**

1. Spring scans for @Configuration classes at startup
2. Processes @Bean methods and registers beans in ApplicationContext
3. Beans are available for dependency injection throughout the app

**When to use:** For configuration classes that create and configure beans.

---

### @Bean

**Purpose:** Declares a method that produces a bean managed by Spring.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http.build();
}
```

**Lifecycle:**

1. Spring calls the method once (singleton by default)
2. Return value is registered as a bean
3. Bean name = method name (can override with @Bean(name = "customName"))

**Scope options:**

- `@Scope("singleton")` - One instance (default)
- `@Scope("prototype")` - New instance each time
- `@Scope("request")` - One per HTTP request (web apps)
- `@Scope("session")` - One per HTTP session

---

### @Component, @Service, @Repository, @Controller

**Purpose:** Stereotype annotations for automatic component detection.

```java
@Component      // Generic component
@Service        // Business logic layer
@Repository     // Data access layer
@Controller     // Web MVC controller
@RestController // REST API controller (@Controller + @ResponseBody)
```

**Difference:**

| Annotation      | Layer    | Special Feature       |
| --------------- | -------- | --------------------- |
| @Component      | Generic  | None                  |
| @Service        | Business | Semantic clarity      |
| @Repository     | Data     | Exception translation |
| @Controller     | Web      | View resolution       |
| @RestController | REST API | Auto JSON conversion  |

**How auto-detection works:**

```java
@SpringBootApplication  // Includes @ComponentScan
public class DemoApplication {
    // Scans current package + sub-packages for components
}
```

---

## Dependency Injection

### @Autowired (Traditional)

**Purpose:** Tells Spring to inject a dependency.

```java
@Service
public class UserService {
    @Autowired  // Field injection (not recommended)
    private UserRepository userRepository;
}
```

**Injection types:**

1. **Field Injection** (not recommended)

```java
@Autowired
private UserRepository userRepository;
```

- ❌ Hard to test (can't inject mocks)
- ❌ Hidden dependencies

2. **Setter Injection**

```java
@Autowired
public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

- ✅ Optional dependencies
- ❌ Mutable

3. **Constructor Injection** (recommended)

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired  // Optional since Spring 4.3
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

- ✅ Immutable (final fields)
- ✅ Easy to test
- ✅ Required dependencies clear

---

### @RequiredArgsConstructor (Lombok - Best Practice)

**Purpose:** Generates constructor for all `final` fields.

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // Constructor auto-generated with both parameters
}
```

**Generated code:**

```java
public UserService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}
```

**Why it's better:**

- ✅ Less boilerplate
- ✅ Enforces immutability (final fields)
- ✅ Automatic dependency injection
- ✅ Easy to add new dependencies

---

## JPA Hibernate

### @Entity & @Table

**Purpose:** Marks a class as a database table.

```java
@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User {
    // ...
}
```

**@Entity:** Required for all JPA entities.
**@Table:** Optional customization (name, indexes, unique constraints).

**Naming convention:**

- Class `User` → table `user` (default)
- Class `OrderItem` → table `order_item` (snake_case)

---

### @Id & @GeneratedValue

**Purpose:** Defines primary key and generation strategy.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**Strategies:**

| Strategy | Description             | Use Case                                |
| -------- | ----------------------- | --------------------------------------- |
| IDENTITY | Database auto-increment | PostgreSQL SERIAL, MySQL AUTO_INCREMENT |
| SEQUENCE | Database sequence       | Oracle, PostgreSQL                      |
| TABLE    | Separate table for IDs  | Portable (slow)                         |
| AUTO     | Let JPA choose          | Quick start                             |

**PostgreSQL example:**

```sql
-- IDENTITY strategy creates:
CREATE TABLE users (
    id SERIAL PRIMARY KEY,  -- Auto-increment
    email VARCHAR(255)
);
```

---

### @Column

**Purpose:** Customizes column mapping.

```java
@Column(name = "email_address",
        nullable = false,
        unique = true,
        length = 100,
        columnDefinition = "VARCHAR(100) DEFAULT 'unknown'")
private String email;
```

**Options:**

- `name`: Column name in database
- `nullable`: Allow NULL values
- `unique`: Unique constraint
- `length`: String length
- `precision`/`scale`: For decimals
- `insertable`/`updatable`: Control write operations

---

### Relationships

#### @OneToMany & @ManyToOne

**Purpose:** Defines one-to-many relationships.

```java
// User has many orders
@Entity
public class User {
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}

// Order belongs to user
@Entity
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

**Key concepts:**

1. **mappedBy:** Indicates the non-owning side (no foreign key)

   - Use on the "one" side of @OneToMany
   - Value = field name on the other side

2. **cascade:** Operations to propagate

   - `ALL`: Persist, merge, remove, refresh, detach
   - `PERSIST`: Save child when saving parent
   - `REMOVE`: Delete child when deleting parent

3. **orphanRemoval:** Delete child when removed from collection

   ```java
   user.getOrders().remove(order);  // Deletes order from database
   ```

4. **fetch:** Loading strategy
   - `LAZY` (default for collections): Load on access
   - `EAGER`: Load immediately with parent

---

#### @ManyToMany

**Purpose:** Many-to-many relationships (requires join table).

```java
@Entity
public class Student {
    @ManyToMany
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}

@Entity
public class Course {
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

**Creates join table:**

```sql
CREATE TABLE student_courses (
    student_id BIGINT,
    course_id BIGINT,
    PRIMARY KEY (student_id, course_id)
);
```

---

#### @Embedded & @Embeddable

**Purpose:** Embed value objects in entities (composition, not separate table).

```java
@Embeddable
@Getter
@Setter
public class Address {
    private String street;
    private String city;
    private String zipCode;
}

@Entity
public class User {
    @Embedded
    private Address address;
}
```

**Generated table:**

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    street VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(255)
);
```

**When to use:**

- Value objects (Address, Money, Dimensions)
- No separate identity needed
- Always part of parent entity

---

### @Enumerated

**Purpose:** Map Java enums to database.

```java
public enum UserRole {
    CUSTOMER, SELLER, ADMIN
}

@Entity
public class User {
    @Enumerated(EnumType.STRING)  // Recommended
    private UserRole role;
}
```

**Strategies:**

| Type    | Storage   | Example    | Issue                           |
| ------- | --------- | ---------- | ------------------------------- |
| STRING  | Enum name | "CUSTOMER" | Safe, refactor-friendly         |
| ORDINAL | Index     | 0, 1, 2    | ❌ Breaks if enum order changes |

**Always use STRING** to avoid data corruption when reordering enums.

---

### Auditing Annotations

#### @CreationTimestamp & @UpdateTimestamp (Hibernate)

```java
@CreationTimestamp
private LocalDateTime createdAt;

@UpdateTimestamp
private LocalDateTime updatedAt;
```

**How it works:** Hibernate automatically sets timestamps on insert/update.

#### @CreatedDate & @LastModifiedDate (JPA)

```java
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Requires:**

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig { }
```

---

## Validation

### @Valid

**Purpose:** Triggers validation on request body.

```java
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody UserDTO dto) {
    // If validation fails, Spring returns 400 Bad Request
}
```

**Without @Valid:** No validation occurs, even if fields have @NotBlank, etc.

---

### Field Validations

```java
@NotNull(message = "ID cannot be null")
private Long id;

@NotBlank(message = "Name is required")
private String name;

@Email(message = "Invalid email format")
private String email;

@Size(min = 8, max = 100, message = "Password must be 8-100 characters")
private String password;

@Min(value = 0, message = "Price cannot be negative")
private BigDecimal price;

@Max(value = 5, message = "Rating cannot exceed 5")
private Integer rating;

@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone")
private String phone;

@Past(message = "Date of birth must be in the past")
private LocalDate dateOfBirth;

@Future(message = "Event date must be in the future")
private LocalDateTime eventDate;
```

**Validation process:**

1. Client sends JSON → Spring deserializes to DTO
2. Spring validates fields (if @Valid present)
3. If validation fails → MethodArgumentNotValidException
4. GlobalExceptionHandler catches exception → Returns 400 with errors

---

## Lombok

### @Data

**Purpose:** All-in-one for DTOs.

```java
@Data
public class UserDTO {
    private Long id;
    private String email;
}
```

**Generates:**

- Getters for all fields
- Setters for non-final fields
- `toString()`
- `equals()` and `hashCode()`
- `@RequiredArgsConstructor` for final fields

**⚠️ Warning:** Don't use on JPA entities (causes recursion in relationships).

---

### @Getter & @Setter

**Purpose:** Fine-grained control over accessors.

```java
@Getter
@Setter
public class User {
    private Long id;
    private String email;
}
```

**Class-level:** Applies to all fields.
**Field-level:** Override for specific fields.

```java
@Getter
public class User {
    private Long id;

    @Setter(AccessLevel.NONE)  // No setter generated
    private String createdAt;
}
```

---

### @Builder

**Purpose:** Fluent API for object creation.

```java
@Builder
public class User {
    private String email;
    private String firstName;
    private String lastName;
}

// Usage:
User user = User.builder()
    .email("test@example.com")
    .firstName("John")
    .lastName("Doe")
    .build();
```

**Benefits:**

- ✅ Readable code
- ✅ Immutable objects
- ✅ Optional fields (no need for multiple constructors)

**With JPA:**

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // Need all 3: NoArgs for JPA, AllArgs for Builder, Builder for usage
}
```

---

### @Slf4j

**Purpose:** Automatic logger creation.

```java
@Slf4j
@Service
public class UserService {
    public void createUser() {
        log.info("Creating user...");
        log.debug("Debug details: {}", data);
        log.error("Error occurred", exception);
    }
}
```

**Generates:**

```java
private static final Logger log = LoggerFactory.getLogger(UserService.class);
```

**Log levels (least to most severe):**

- TRACE: Very detailed
- DEBUG: Debugging info
- INFO: General info
- WARN: Warnings
- ERROR: Errors

---

## Security

### @EnableWebSecurity

**Purpose:** Enables Spring Security for the application.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Security configuration
}
```

**What it does:**

- Activates security filters
- Enables @PreAuthorize, @Secured, etc.
- Sets up authentication

---

### @EnableMethodSecurity

**Purpose:** Enables method-level security annotations.

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig { }
```

**Enables:**

- @PreAuthorize
- @PostAuthorize
- @Secured
- @RolesAllowed

---

### @PreAuthorize

**Purpose:** Security checks before method execution.

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public void deleteUser(@PathVariable Long id) {
    // Only ADMIN role can execute
}

@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
public void manageProducts() {
    // ADMIN or SELLER can execute
}

@PreAuthorize("authentication.principal.id == #userId")
public void updateProfile(@PathVariable Long userId) {
    // Users can only update their own profile
}
```

**SpEL expressions:**

- `hasRole('ADMIN')`: Check single role
- `hasAnyRole('ADMIN', 'SELLER')`: Check multiple roles
- `hasAuthority('READ')`: Check authority
- `authentication.principal.username`: Current username
- `#paramName`: Method parameter

---

## REST API

### @RestController

**Purpose:** Combines @Controller + @ResponseBody.

```java
@RestController  // All methods return data (JSON), not views
@RequestMapping("/api/users")
public class UserController { }
```

**vs @Controller:**

```java
@Controller  // Returns views (HTML templates)
public class WebController {
    @GetMapping("/home")
    public String home() {
        return "home";  // Returns home.html template
    }
}
```

---

### HTTP Method Mappings

```java
@GetMapping("/{id}")       // Read
@PostMapping               // Create
@PutMapping("/{id}")       // Update (full)
@PatchMapping("/{id}")     // Update (partial)
@DeleteMapping("/{id}")    // Delete
```

**RESTful conventions:**

- GET: Read (no body)
- POST: Create (body contains data)
- PUT: Replace entire resource
- PATCH: Update specific fields
- DELETE: Remove resource

---

### Parameter Binding

```java
@GetMapping("/users/{id}")
public User getUser(
    @PathVariable Long id,                     // /users/123
    @RequestParam(required = false) String filter,  // ?filter=active
    @RequestHeader("Authorization") String token,  // HTTP header
    @RequestBody UserDTO dto                   // JSON body (POST/PUT)
) { }
```

**@PathVariable:** Extract from URL path.
**@RequestParam:** Extract from query string.
**@RequestHeader:** Extract from HTTP headers.
**@RequestBody:** Deserialize JSON/XML to object.

---

## Async & Scheduling

### @EnableAsync

**Purpose:** Enables asynchronous method execution.

```java
@Configuration
@EnableAsync
public class AsyncConfig { }
```

**How it works:**

1. Spring creates a thread pool
2. @Async methods run in separate threads
3. Caller doesn't wait for completion

---

### @Async

**Purpose:** Execute method asynchronously.

```java
@Async
public void sendEmail(String to, String body) {
    // Runs in background thread
    mailSender.send(message);
}

// Caller doesn't wait:
emailService.sendEmail("test@example.com", "Hello");
System.out.println("Email sending initiated, continuing...");
```

**Return types:**

- `void`: Fire and forget
- `Future<T>`: Get result later
- `CompletableFuture<T>`: Modern async (recommended)

```java
@Async
public CompletableFuture<String> processData() {
    String result = heavyComputation();
    return CompletableFuture.completedFuture(result);
}
```

---

## Transactions

### @Transactional

**Purpose:** Manage database transactions automatically.

```java
@Service
@Transactional  // All methods are transactional
public class OrderService {

    @Transactional
    public Order createOrder(OrderRequest request) {
        // 1. Create order
        Order order = orderRepository.save(order);

        // 2. Reduce stock
        product.setStock(product.getStock() - quantity);

        // 3. Create payment
        paymentService.processPayment(order);

        // If any step fails, ALL rollback
        return order;
    }
}
```

**Behavior:**

- ✅ Commit if method completes successfully
- ✅ Rollback if exception thrown
- ✅ Database consistency guaranteed

**Options:**

```java
@Transactional(
    propagation = Propagation.REQUIRED,    // Join existing or create new
    isolation = Isolation.READ_COMMITTED,  // Isolation level
    readOnly = true,                       // Optimize read-only queries
    rollbackFor = Exception.class,         // Rollback on all exceptions
    timeout = 30                           // Timeout in seconds
)
```

**Propagation:**

- `REQUIRED`: Use existing transaction or create new (default)
- `REQUIRES_NEW`: Always create new transaction
- `NESTED`: Nested within existing transaction

---

## Value Injection

### @Value

**Purpose:** Inject values from application.yml/properties.

```java
@Value("${jwt.secret}")
private String jwtSecret;

@Value("${app.otp.console-output:false}")
private boolean consoleOutput;  // Default: false

@Value("${server.port}")
private int serverPort;

@Value("#{systemProperties['user.home']}")
private String userHome;  // SpEL expression
```

**Syntax:**

- `${property.name}`: From properties file
- `${property:default}`: With default value
- `#{expression}`: SpEL expression

---

## Profile-Specific Beans

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        // H2 in-memory database for dev
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        // PostgreSQL for production
    }
}
```

**Activation:**

- `spring.profiles.active=dev` in application.yml
- `-Dspring.profiles.active=prod` JVM argument
- `export SPRING_PROFILES_ACTIVE=prod` environment variable

---

This covers all major annotations used in the project! 🎉
