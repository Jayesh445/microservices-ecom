# E-Commerce Application - Complete Guide & Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Annotations Explained](#annotations-explained)
4. [Configuration Files](#configuration-files)
5. [Profile Management (Dev & Prod)](#profile-management-dev--prod)
6. [Security Implementation](#security-implementation)
7. [Module Breakdown](#module-breakdown)
8. [Running the Application](#running-the-application)

---

## Project Overview

This is a **comprehensive e-commerce application** built with Spring Boot, implementing:

- ✅ Modular architecture (9 domain modules)
- ✅ JWT authentication with access & refresh tokens
- ✅ OTP-based email verification
- ✅ Role-based access control (RBAC)
- ✅ PostgreSQL database with JPA/Hibernate
- ✅ RESTful API design
- ✅ Development & Production profiles

**Technology Stack:**

- Java 21
- Spring Boot 4.0.1
- PostgreSQL 16
- JWT (jjwt 0.12.5)
- Lombok
- Hibernate/JPA

---

## Architecture & Design Patterns

### 1. **Layered Architecture**

```
Controller Layer  →  Service Layer  →  Repository Layer  →  Database
     ↓                    ↓                  ↓
  REST APIs       Business Logic      Data Access (JPA)
```

### 2. **Modular Monolith Pattern**

Each business domain is organized in its own package:

- `user/` - User management
- `product/` - Product catalog
- `order/` - Order processing
- `cart/` - Shopping cart
- `payment/` - Payment handling
- `address/` - Address management
- `review/` - Product reviews
- `auth/` - Authentication
- `common/` - Shared utilities

### 3. **Design Patterns Used**

#### Repository Pattern

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

**Benefits:** Abstraction over data access, testability

#### Service Layer Pattern

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    // Business logic here
}
```

**Benefits:** Separation of concerns, reusability

#### DTO Pattern (Data Transfer Object)

```java
public class UserRegistrationRequest {
    private String email;
    private String password;
    // ... fields
}
```

**Benefits:** Decouples API from database entities, validation

#### Builder Pattern (Lombok @Builder)

```java
User user = User.builder()
    .email("test@example.com")
    .firstName("John")
    .build();
```

**Benefits:** Clean object creation, immutability

---

## Annotations Explained

### Spring Core Annotations

#### `@Configuration`

```java
@Configuration
public class SecurityConfig { }
```

**Purpose:** Marks a class as a source of bean definitions for the Spring IoC container.
**When to use:** For configuration classes that define beans.

#### `@Bean`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Purpose:** Indicates that a method produces a bean to be managed by Spring.
**Lifecycle:** Spring calls this method and manages the returned object.

#### `@Component`

```java
@Component
public class JwtTokenProvider { }
```

**Purpose:** Generic stereotype for any Spring-managed component.
**Auto-detection:** Spring automatically detects and registers as a bean.

#### `@Service`

```java
@Service
public class UserService { }
```

**Purpose:** Specialization of @Component for service layer classes.
**Semantics:** Indicates this class contains business logic.

#### `@Repository`

```java
public interface UserRepository extends JpaRepository<User, Long> { }
```

**Purpose:** Specialization of @Component for data access layer.
**Extra Feature:** Automatic exception translation to Spring's DataAccessException.

#### `@Controller` vs `@RestController`

```java
@RestController  // = @Controller + @ResponseBody
@RequestMapping("/api/users")
public class UserController { }
```

**@RestController:** Automatically serializes return values to JSON/XML.
**When to use:** For RESTful web services.

---

### Dependency Injection Annotations

#### `@Autowired` (Traditional)

```java
@Autowired
private UserRepository userRepository;
```

**Purpose:** Automatic dependency injection by Spring.
**Types:** Constructor, Field, Setter injection.

#### `@RequiredArgsConstructor` (Lombok - Modern Approach)

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;  // Automatically injected
}
```

**Purpose:** Generates constructor for all `final` fields.
**Benefits:** Immutability, testability, no need for @Autowired.

---

### JPA/Hibernate Annotations

#### `@Entity`

```java
@Entity
@Table(name = "users")
public class User { }
```

**Purpose:** Marks a class as a JPA entity (database table).
**@Table:** Specifies the table name (optional, defaults to class name).

#### `@Id` & `@GeneratedValue`

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**@Id:** Primary key field.
**@GeneratedValue:** Auto-increment strategy.
**IDENTITY:** Uses database auto-increment (PostgreSQL SERIAL).

#### `@Column`

```java
@Column(nullable = false, unique = true, length = 100)
private String email;
```

**Purpose:** Defines column properties.
**Options:** nullable, unique, length, name, columnDefinition.

#### `@OneToMany` & `@ManyToOne`

```java
// User side (one user has many orders)
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<Order> orders = new ArrayList<>();

// Order side (many orders belong to one user)
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

**mappedBy:** Indicates the owning side (avoids duplicate foreign key).
**cascade:** Operations to cascade (persist, remove, etc.).
**JoinColumn:** Foreign key column name.

#### `@Embedded` & `@Embeddable`

```java
@Embeddable
public class ProductDimensions {
    private Double length;
    private Double width;
    private Double height;
}

@Entity
public class Product {
    @Embedded
    private ProductDimensions dimensions;
}
```

**Purpose:** Embed a value object in an entity (composition, not separate table).

#### `@Enumerated`

```java
@Enumerated(EnumType.STRING)
private UserRole role;
```

**EnumType.STRING:** Stores enum as string (e.g., "CUSTOMER", "ADMIN").
**EnumType.ORDINAL:** Stores as integer (not recommended - fragile).

#### `@CreationTimestamp` & `@UpdateTimestamp`

```java
@CreationTimestamp
private LocalDateTime createdAt;

@UpdateTimestamp
private LocalDateTime updatedAt;
```

**Purpose:** Automatically set timestamps on entity creation/update.
**Source:** Hibernate annotation (not JPA standard).

#### `@EnableJpaAuditing`

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig { }
```

**Purpose:** Enables JPA auditing features.
**Works with:** @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy.

---

### Validation Annotations

#### `@Valid`

```java
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) { }
```

**Purpose:** Triggers validation on the request body.
**Validation:** Checks constraints like @NotBlank, @Email, @Size.

#### Field Validation

```java
@NotBlank(message = "Email is required")
@Email(message = "Invalid email format")
private String email;

@Size(min = 8, max = 100, message = "Password must be 8-100 characters")
private String password;

@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
private String phoneNumber;
```

**Common Annotations:**

- `@NotNull`: Field cannot be null
- `@NotBlank`: String cannot be null/empty/whitespace
- `@Email`: Valid email format
- `@Size`: Length constraints
- `@Min/@Max`: Numeric range
- `@Pattern`: Regex validation

---

### Lombok Annotations

#### `@Data`

```java
@Data
public class UserDTO {
    private Long id;
    private String email;
}
```

**Generates:** Getters, Setters, toString(), equals(), hashCode().
**Warning:** Use carefully with JPA entities (can cause recursion issues).

#### `@Getter` & `@Setter`

```java
@Getter
@Setter
public class User { }
```

**Purpose:** Generates only getters/setters (more control than @Data).

#### `@NoArgsConstructor` & `@AllArgsConstructor`

```java
@NoArgsConstructor   // Empty constructor
@AllArgsConstructor  // Constructor with all fields
public class User { }
```

**Use case:** JPA requires no-args constructor; all-args for builder.

#### `@Builder`

```java
@Builder
public class User { }

// Usage:
User user = User.builder()
    .email("test@example.com")
    .firstName("John")
    .build();
```

**Purpose:** Fluent API for object creation.
**Benefits:** Readable, handles optional fields well.

#### `@Slf4j`

```java
@Slf4j
public class UserService {
    public void doSomething() {
        log.info("Doing something...");
    }
}
```

**Generates:** `private static final Logger log = LoggerFactory.getLogger(UserService.class);`
**Benefits:** Less boilerplate for logging.

---

### Security Annotations

#### `@EnableWebSecurity`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig { }
```

**Purpose:** Enables Spring Security's web security support.

#### `@EnableMethodSecurity`

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig { }
```

**Purpose:** Enables method-level security annotations like @PreAuthorize.

#### `@PreAuthorize`

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteUser(@PathVariable Long id) { }
```

**Purpose:** Secures methods based on SpEL expressions.
**Common expressions:**

- `hasRole('ADMIN')`: User has ADMIN role
- `hasAnyRole('ADMIN', 'SELLER')`: User has any of these roles
- `authentication.principal.id == #id`: User can only access their own data

---

### Async & Scheduling Annotations

#### `@EnableAsync`

```java
@Configuration
@EnableAsync
public class AsyncConfig { }
```

**Purpose:** Enables asynchronous method execution.
**Use case:** Send emails, process background tasks without blocking.

#### `@Async`

```java
@Async
public void sendOtpEmail(String to, String otp) {
    // Email sending logic (runs in separate thread)
}
```

**Purpose:** Method runs asynchronously in a thread pool.
**Benefits:** Non-blocking, improves response times.

---

### REST Annotations

#### `@RestController` & `@RequestMapping`

```java
@RestController
@RequestMapping("/api/users")
public class UserController { }
```

**@RestController:** Combines @Controller + @ResponseBody.
**@RequestMapping:** Base path for all endpoints in controller.

#### HTTP Method Mappings

```java
@GetMapping("/{id}")                    // GET /api/users/1
@PostMapping("/register")               // POST /api/users/register
@PutMapping("/{id}")                    // PUT /api/users/1
@DeleteMapping("/{id}")                 // DELETE /api/users/1
@PatchMapping("/{id}")                  // PATCH /api/users/1
```

#### Parameter Annotations

```java
@GetMapping("/{id}")
public User getUser(
    @PathVariable Long id,              // From URL path
    @RequestParam String filter,        // Query parameter (?filter=value)
    @RequestBody UserDTO dto,           // Request body (JSON)
    @RequestHeader("Authorization") String token  // HTTP header
) { }
```

---

### Transaction Management

#### `@Transactional`

```java
@Service
@Transactional
public class OrderService {

    @Transactional
    public Order createOrder(OrderRequest request) {
        // All database operations here run in a transaction
        // Rollback if any exception occurs
    }
}
```

**Purpose:** Manages database transactions automatically.
**Features:**

- Auto-commit on success
- Auto-rollback on exception
- Isolation levels (READ_COMMITTED, etc.)
- Propagation behavior (REQUIRED, REQUIRES_NEW, etc.)

---

### Value Injection

#### `@Value`

```java
@Value("${jwt.secret}")
private String jwtSecret;

@Value("${app.otp.console-output:false}")  // Default: false
private boolean consoleOutput;
```

**Purpose:** Injects values from application.yml/properties.
**Syntax:** `${property.name:defaultValue}`

---

## Configuration Files

### Application Profiles Structure

```
src/main/resources/
├── application.yml          # Common configuration (all profiles)
├── application-dev.yml      # Development-specific config
└── application-prod.yml     # Production-specific config
```

### Profile Selection

#### Default Profile (Dev)

```yaml
# application.yml - Add this to set default profile
spring:
  profiles:
    active: dev # Use 'dev' profile by default
```

#### Run with Specific Profile

**Maven:**

```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**Java JAR:**

```bash
java -jar -Dspring.profiles.active=prod ecommerce-app.jar
```

**Environment Variable:**

```bash
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

**IDE (IntelliJ/Eclipse):**

- Edit Run Configuration
- Add VM option: `-Dspring.profiles.active=dev`

---

## Profile Management (Dev & Prod)

### Development Profile (`application-dev.yml`)

**Key Features:**

- ✅ Verbose logging (DEBUG level)
- ✅ Show SQL queries with formatting
- ✅ Auto-create/update database schema
- ✅ **OTP printed to console**
- ✅ Detailed error messages

```yaml
app:
  mode: development
  otp:
    console-output: true # OTP visible in console!
```

**Benefits:**

- Fast development iteration
- Easy debugging
- No email configuration needed initially

### Production Profile (`application-prod.yml`)

**Key Features:**

- ✅ Minimal logging (INFO/WARN level)
- ✅ Environment variable configuration
- ✅ Only validate database schema (no auto-creation)
- ✅ Hide SQL queries
- ✅ Connection pooling (HikariCP)
- ✅ Log files with rotation

```yaml
app:
  mode: production
  otp:
    console-output: false # Never show OTP in production!
```

**Security Best Practices:**

```yaml
spring:
  datasource:
    url: ${DATABASE_URL} # From environment
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

**Why Environment Variables?**

- Secrets not stored in code
- Different values per deployment
- Works with Docker, Kubernetes, cloud platforms

---

## Security Implementation

### JWT Authentication Flow

```
1. User Login → AuthService validates credentials
2. AuthService generates Access Token (15 min) & Refresh Token (7 days)
3. Client stores tokens (localStorage or httpOnly cookie)
4. Client sends Access Token in header: "Authorization: Bearer <token>"
5. JwtAuthenticationFilter intercepts request
6. Filter validates token & sets Spring Security context
7. Controller processes request with authenticated user
8. Access Token expires → Client uses Refresh Token to get new Access Token
```

### Security Filter Chain

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF (stateless JWT)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()  // Public endpoints
            .anyRequest().authenticated()                  // All others require auth
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No sessions
        )
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
}
```

**Key Components:**

1. **JwtTokenProvider**

   - Generates tokens with claims (username, expiration)
   - Validates tokens using signature
   - Extracts claims from tokens

2. **JwtAuthenticationFilter**

   - Intercepts every HTTP request
   - Extracts JWT from Authorization header
   - Validates token and loads user
   - Sets SecurityContext for downstream processing

3. **CustomUserDetailsService**
   - Loads user from database by email
   - Converts to Spring Security UserDetails
   - Provides authorities (roles)

---

## Module Breakdown

### 1. User Module (`works.jayesh.demo.user`)

**Purpose:** User management and profile operations.

**Components:**

- `User` entity - Core user data with relationships
- `UserRole` enum - CUSTOMER, SELLER, ADMIN
- `UserStatus` enum - ACTIVE, INACTIVE, BANNED
- `UserRepository` - Data access with custom queries
- `UserService` - Business logic (CRUD, search)
- `UserController` - REST endpoints with @PreAuthorize

**Key Features:**

- Role-based access control
- User search and filtering
- Profile management
- Audit trails (createdAt, updatedAt)

---

### 2. Authentication Module (`works.jayesh.demo.auth`)

**Purpose:** Handle registration, login, OTP verification, token management.

**Components:**

- `AuthService` - Core authentication logic
- `OtpService` - Generate & verify OTP
- `EmailService` - Async email sending
- `AuthController` - Public authentication endpoints

**Authentication Flows:**

#### Registration with OTP:

```
POST /api/auth/register/request-otp → Send OTP
POST /api/auth/register/verify-otp  → Verify & create user
```

#### Registration with Password:

```
POST /api/auth/register/password → Create user & return tokens
```

#### Login with OTP:

```
POST /api/auth/login/request-otp → Send OTP
POST /api/auth/login/verify-otp  → Verify & return tokens
```

#### Login with Password:

```
POST /api/auth/login/password → Validate & return tokens
```

#### Token Refresh:

```
POST /api/auth/refresh → New access token
```

---

### 3. Product Module (`works.jayesh.demo.product`)

**Purpose:** Product catalog management.

**Components:**

- `Product` entity - Product details with embedded dimensions
- `Category` entity - Hierarchical categories
- `ProductStatus` enum - DRAFT, ACTIVE, OUT_OF_STOCK, DISCONTINUED
- `ProductDimensions` - Embeddable value object (length, width, height)
- Repositories with custom queries (search, filter by price/category)

**Key Features:**

- Product variants (SKU-based)
- Category hierarchy (parent-child)
- Stock management
- Price with discounts
- Average rating calculation
- Featured products

---

### 4. Order Module (`works.jayesh.demo.order`)

**Purpose:** Order processing and lifecycle management.

**Components:**

- `Order` entity - Order header
- `OrderItem` entity - Line items
- `OrderStatus` enum - PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
- `OrderService` - Stock validation, price calculation

**Order Processing:**

```
1. Create order → Validate stock
2. Calculate totals (subtotal + tax + shipping - discount)
3. Create OrderItems
4. Reduce product stock
5. Create Payment
6. Update status
```

---

### 5. Cart Module (`works.jayesh.demo.cart`)

**Purpose:** Shopping cart management.

**Components:**

- `Cart` entity - One per user
- `CartItem` entity - Product + quantity
- `CartService` - Add, update, remove items

**Key Features:**

- Stock validation before adding
- Automatic price calculation
- Clear cart after order

---

### 6. Payment Module (`works.jayesh.demo.payment`)

**Purpose:** Payment processing.

**Components:**

- `Payment` entity - Payment record
- `PaymentMethod` enum - CREDIT_CARD, DEBIT_CARD, UPI, WALLET, COD
- `PaymentStatus` enum - PENDING, COMPLETED, FAILED, REFUNDED
- `PaymentService` - Process & refund with gateway simulation

---

### 7. Address Module (`works.jayesh.demo.address`)

**Purpose:** User address management.

**Components:**

- `Address` entity - Full address details
- `AddressType` enum - HOME, WORK, OTHER
- Default address handling

---

### 8. Review Module (`works.jayesh.demo.review`)

**Purpose:** Product reviews and ratings.

**Components:**

- `Review` entity - Rating (1-5), title, comment
- Review approval workflow
- Update product average rating

---

### 9. Common Module (`works.jayesh.demo.common`)

**Purpose:** Shared utilities and exception handling.

**Components:**

- `GlobalExceptionHandler` - @RestControllerAdvice
- `ApiResponse<T>` - Standardized response wrapper
- Custom exceptions:
  - `ResourceNotFoundException`
  - `DuplicateResourceException`
  - `InsufficientStockException`

**Response Format:**

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

---

## Running the Application

### Prerequisites

1. **Java 21** installed
2. **PostgreSQL** running on localhost:5432
3. Database `ecommerce_db` created
4. **Maven** (included via mvnw wrapper)

### Setup Database

```sql
CREATE DATABASE ecommerce_db;
```

### Development Mode (Default)

```bash
# Run with development profile
./mvnw spring-boot:run

# Or explicitly
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**What happens in dev mode:**

- ✅ OTP printed to console (no email config needed!)
- ✅ SQL queries shown with formatting
- ✅ Detailed logging (DEBUG level)
- ✅ Database schema auto-created/updated

**Console Output Example:**

```
╔════════════════════════════════════════╗
║        DEVELOPMENT MODE - OTP          ║
╠════════════════════════════════════════╣
║  Email: test@example.com               ║
║  OTP:   123456                         ║
║  Valid for: 5 minutes                  ║
╚════════════════════════════════════════╝
```

### Production Mode

```bash
# Set environment variables first
export DATABASE_URL=jdbc:postgresql://prod-host:5432/ecommerce_db
export DATABASE_USERNAME=prod_user
export DATABASE_PASSWORD=secure_password
export MAIL_USERNAME=noreply@company.com
export MAIL_PASSWORD=app_password

# Run with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**What happens in prod mode:**

- ✅ OTP sent via email (never in console)
- ✅ Minimal logging (INFO/WARN)
- ✅ Database schema only validated
- ✅ Configuration from environment variables
- ✅ Log files with rotation

### Build & Run JAR

```bash
# Build
./mvnw clean package

# Run
java -jar -Dspring.profiles.active=dev target/ecommerce-app-0.0.1-SNAPSHOT.jar
```

---

## Testing the Application

### 1. Test Registration with OTP (Dev Mode)

```bash
# Request OTP
curl -X POST http://localhost:8080/api/auth/register/request-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# Check console for OTP (in dev mode), then verify
curl -X POST http://localhost:8080/api/auth/register/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "firstName":"John",
    "lastName":"Doe",
    "otp":"<OTP_FROM_CONSOLE>"
  }'
```

### 2. Test Login with Password

```bash
curl -X POST http://localhost:8080/api/auth/login/password \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "password":"YourPassword123"
  }'
```

### 3. Use Access Token

```bash
# Save token from login response
TOKEN="<your-access-token>"

# Access protected endpoint
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

---

## Summary of Key Concepts

### Why @EnableAsync?

Enables asynchronous method execution. Email sending runs in background without blocking the main thread.

### Why @Transactional?

Ensures database operations are atomic - all succeed or all rollback.

### Why @RequiredArgsConstructor?

Generates constructor for dependency injection (cleaner than @Autowired).

### Why Profiles (dev/prod)?

Different configurations for development and production environments.

### Why @Slf4j?

Automatic logger creation for consistent logging across application.

### Why @PreAuthorize?

Method-level security to restrict access based on user roles.

### Why JPA Auditing?

Automatically track creation and modification timestamps.

### Why Builder Pattern?

Clean, readable object creation with optional fields.

### Why DTO Pattern?

Separate API contracts from database entities, better validation.

---

## Project Statistics

- **Total Modules:** 9 (user, product, order, cart, payment, address, review, auth, common)
- **Entities:** 10+ (User, Product, Order, Cart, Payment, Address, Review, Category, etc.)
- **REST Endpoints:** 50+ (CRUD operations across all modules)
- **Authentication Methods:** 4 (password login, OTP login, password registration, OTP registration)
- **Security:** JWT-based, role-based access control
- **Email Features:** OTP verification, welcome emails (async)
- **Profiles:** 2 (dev, prod)

---

## Next Steps

1. ✅ Run in dev mode and test OTP in console
2. ✅ Configure email for production
3. ✅ Add integration tests
4. ✅ Deploy to cloud (AWS, Azure, GCP)
5. ✅ Add Swagger/OpenAPI documentation
6. ✅ Implement caching (Redis)
7. ✅ Add rate limiting
8. ✅ Set up CI/CD pipeline

---

**Happy Coding! 🚀**
