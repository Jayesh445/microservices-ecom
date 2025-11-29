# Comprehensive Testing Guide

## Overview

This Spring Boot E-Commerce application has been thoroughly tested with **296 total tests** covering unit tests, integration tests, and validation tests. The testing suite ensures production-ready quality with comprehensive flow coverage.

## Test Suite Summary

### Total Test Coverage

```
✅ 296 Tests - 100% Passing
├── 270 Unit Tests (Controllers + Services)
│   ├── 135 Controller Tests (API Layer)
│   ├── 120 Service Tests (Business Logic)
│   └── 15 Validation Tests (DTO Validation)
└── 26 Integration Tests (End-to-End Flows)
    ├── 12 E-Commerce Flow Tests
    └── 14 Product Management Flow Tests
```

## Test Structure

### 1. Unit Tests (270 Tests)

#### Controller Tests (135 Tests)

- **UserController**: 16 tests - API endpoint validation, error handling
- **ProductController**: 16 tests - CRUD operations, validation
- **CategoryController**: 14 tests - Category management
- **ReviewController**: 18 tests - Review lifecycle
- **OrderController**: 14 tests - Order creation and management
- **CartController**: 14 tests - Shopping cart operations
- **AddressController**: 15 tests - Address CRUD
- **PaymentController**: 14 tests - Payment processing
- **AuthController**: 14 tests - Authentication and authorization

**What Controller Tests Validate:**

- ✓ HTTP status codes (200, 201, 400, 404, 422)
- ✓ Request/response JSON structures
- ✓ Input validation (@Valid annotations)
- ✓ Error response formats
- ✓ Content negotiation
- ✓ Malformed JSON handling
- ✓ Type mismatch errors
- ✓ Resource not found scenarios

#### Service Tests (120 Tests)

- **UserService**: 15 tests - User registration, updates, authentication
- **ProductService**: 12 tests - Product CRUD, stock management
- **CategoryService**: 14 tests - Category hierarchy
- **ReviewService**: 15 tests - Review approval, moderation
- **OrderService**: 12 tests - Order lifecycle, status transitions
- **CartService**: 12 tests - Cart item management
- **AddressService**: 12 tests - Address validation
- **PaymentService**: 12 tests - Payment processing, refunds
- **AuthService**: 16 tests - Token generation, refresh, validation

**What Service Tests Validate:**

- ✓ Business logic correctness
- ✓ Exception handling (ResourceNotFoundException, etc.)
- ✓ Data validation rules
- ✓ State transitions (Order status, Payment status)
- ✓ Conditional logic branches
- ✓ Edge cases and boundary conditions
- ✓ Transactional integrity
- ✓ Logging behavior

#### Validation Tests (15 Tests)

- **DTO Validation**: Tests for all request DTOs
  - Email format validation
  - Required field validation
  - Min/max length validation
  - Numeric range validation
  - Custom validation annotations

### 2. Integration Tests (26 Tests)

#### E-Commerce Flow Integration Tests (12 Tests)

These tests validate complete user journeys with real database transactions:

1. **User Creation Flow**

   - Creates customer and seller users
   - Verifies password encryption
   - Validates role assignments

2. **Product Browsing Flow**

   - Tests category-based product search
   - Verifies pagination works correctly
   - Validates product filtering

3. **Add to Cart Flow**

   - Adds products to cart
   - Calculates cart totals
   - Verifies quantity management

4. **Update Cart Quantity Flow**

   - Modifies item quantities
   - Recalculates totals
   - Validates stock availability

5. **Remove from Cart Flow**

   - Removes items from cart
   - Verifies cart state after deletion

6. **Order Creation Flow**

   - Creates order from cart items
   - Calculates subtotal, tax, shipping
   - Assigns shipping/billing addresses
   - Generates order number

7. **Multiple Items in Cart Flow**

   - Adds multiple products
   - Validates cart total calculation
   - Tests concurrent cart operations

8. **Order History Flow**

   - Creates multiple orders
   - Retrieves user's order history with pagination
   - Validates order filtering

9. **Address Management Flow**

   - Creates multiple addresses (home, work)
   - Uses different addresses in orders
   - Validates address type constraints

10. **Stock Validation Flow**

    - Checks product stock availability
    - Reduces stock after purchase
    - Prevents negative stock

11. **Database Indexes Performance Flow**

    - Tests indexed queries (user_id, category_id, slug, SKU)
    - Verifies query performance with indexes

12. **Complete Purchase Flow (End-to-End)**
    - User browses products → adds to cart → reviews cart → creates order → clears cart
    - Full lifecycle validation with multiple database operations

#### Product Management Flow Integration Tests (14 Tests)

1. **Category Creation Flow**

   - Creates categories
   - Validates slug generation

2. **Product Creation Flow**

   - Seller creates product
   - Assigns to category
   - Sets initial stock

3. **Product Update Flow**

   - Updates name, price, stock
   - Validates changes persist

4. **Product Featured Toggle Flow**

   - Features/unfeatures products
   - Tests featured product queries

5. **Product Activation Flow**

   - Activates/deactivates products
   - Changes status (ACTIVE → ARCHIVED)

6. **Add Review Flow**

   - Customer submits review
   - Stores rating and comment

7. **Product Rating Update Flow**

   - Multiple reviews submitted
   - Calculates average rating
   - Updates totalReviews count

8. **Search by Category Flow**

   - Finds all products in category with pagination
   - Validates results

9. **Search by Seller Flow**

   - Lists seller's products with pagination
   - Verifies seller filtering

10. **Find by Slug Flow**

    - SEO-friendly product lookup
    - Validates unique slugs

11. **Find by SKU Flow**

    - Inventory system lookup
    - Validates unique SKUs

12. **Review Lifecycle Flow**

    - Customer writes review → admin approves → appears on product
    - Tests approval workflow

13. **Featured Products Retrieval Flow**

    - Queries all featured products
    - Validates featured flag filtering

14. **Stock Management Flow**
    - Reduces stock after sale
    - Restocks inventory
    - Tracks totalSold metric

## JaCoCo Code Coverage Report

### How to View Coverage Report

```bash
# Generate fresh coverage data
mvn clean test

# Open report in browser
start target/site/jacoco/index.html  # Windows
open target/site/jacoco/index.html   # Mac
xdg-open target/site/jacoco/index.html  # Linux
```

### Coverage Report Location

📁 `target/site/jacoco/index.html`

### Understanding the Report

#### 1. **Coverage Metrics**

- **Instructions (C0)**: Individual Java bytecode instructions
- **Branches (C1)**: Decision points (if/else, switch, ternary)
- **Lines**: Source code lines
- **Complexity**: Cyclomatic complexity
- **Methods**: Method coverage
- **Classes**: Class coverage

#### 2. **Color Coding**

- 🟢 **Green**: Covered code (executed by tests)
- 🔴 **Red**: Uncovered code (not executed)
- 🟡 **Yellow**: Partially covered branches

#### 3. **Coverage Thresholds**

Our project enforces these minimum coverage requirements:

| Component   | Line Coverage | Branch Coverage |
| ----------- | ------------- | --------------- |
| Services    | 80%           | 75%             |
| Controllers | 60%           | -               |
| Overall     | 70%           | -               |

**Build fails if thresholds are not met!**

#### 4. **Excluded from Coverage**

- DTOs (Data Transfer Objects)
- Entities (JPA model classes)
- Configuration classes
- Exception classes
- DemoApplication (main class)

### Interpreting Coverage Numbers

#### Example: OrderService Coverage

```
Instructions: 85% (142/167)
Branches: 78% (25/32)
Lines: 87% (45/52)
Methods: 90% (9/10)
```

**What this means:**

- ✅ 85% of bytecode instructions executed
- ✅ 78% of if/else branches tested
- ✅ 87% of code lines covered
- ⚠️ 1 method never called in tests

**How to improve:**

1. Click on the class name to see line-by-line coverage
2. Red lines = add tests to cover these scenarios
3. Yellow diamond = add tests for both if/true and if/false cases

## Running Tests

### Run All Tests

```bash
mvn clean test
```

### Run Unit Tests Only

```bash
mvn test -Dtest=*Controller*Test,*Service*Test
```

### Run Integration Tests Only

```bash
mvn test -Dtest=*IntegrationTest
```

### Run Specific Test Class

```bash
mvn test -Dtest=UserControllerTest
mvn test -Dtest=ECommerceFlowIntegrationTest
```

### Run with Coverage Report

```bash
mvn clean test jacoco:report
```

### Skip Tests (Not Recommended)

```bash
mvn clean install -DskipTests
```

## Test Configuration

### Test Profile

Integration tests use the `test` profile with H2 in-memory database:

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate.ddl-auto: create-drop
  logging:
    level:
      root: WARN
      works.jayesh.demo: DEBUG
```

### Test Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## Testing Best Practices Implemented

### 1. **Isolation**

- Each test is independent
- `@Transactional` ensures database rollback
- Mocks used for external dependencies

### 2. **AAA Pattern**

```java
@Test
void testUserCreation() {
    // Arrange
    User user = createTestUser();

    // Act
    User saved = userService.save(user);

    // Assert
    assertThat(saved.getId()).isNotNull();
}
```

### 3. **Descriptive Names**

```java
@Test
@DisplayName("Should return 404 when product not found")
void shouldReturn404WhenProductNotFound() {
    // test implementation
}
```

### 4. **Test Data Builders**

```java
User testUser = User.builder()
    .firstName("John")
    .lastName("Doe")
    .email("john@example.com")
    .role(UserRole.CUSTOMER)
    .build();
```

### 5. **AssertJ for Fluent Assertions**

```java
assertThat(products)
    .isNotEmpty()
    .hasSize(2)
    .allMatch(p -> p.getPrice().compareTo(BigDecimal.ZERO) > 0);
```

## Continuous Integration

### GitHub Actions Workflow (Example)

```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: "21"
      - name: Run tests
        run: mvn clean test
      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

## Troubleshooting Tests

### Common Issues

#### 1. **Tests Fail with "Connection Refused"**

- **Cause**: Application trying to connect to real database
- **Fix**: Ensure `@ActiveProfiles("test")` is present
- **Fix**: Check application-test.yml exists

#### 2. **Flaky Integration Tests**

- **Cause**: Test order dependency or timing issues
- **Fix**: Use `@TestMethodOrder(OrderAnnotation.class)`
- **Fix**: Avoid Thread.sleep(), use proper waits

#### 3. **Coverage Below Threshold**

```
[ERROR] Rule violated for bundle: line coverage is 65%, minimum is 70%
```

- **Fix**: Add more test cases for uncovered branches
- **Fix**: Check JaCoCo report to identify gaps

#### 4. **@Transactional Not Rolling Back**

- **Cause**: Missing test transaction management
- **Fix**: Ensure `spring-boot-starter-test` dependency present
- **Fix**: Add `@SpringBootTest` to integration tests

## Test Maintenance

### When to Update Tests

1. **After Changing Business Logic**

   - Update service tests
   - Update integration tests if flow changes

2. **After Adding New Endpoints**

   - Add controller tests
   - Add integration tests for new flows

3. **After Changing DTOs**

   - Update validation tests
   - Update controller tests

4. **After Database Schema Changes**
   - Update integration tests
   - Verify entity mappings

## Performance Considerations

### Test Execution Times

```
Unit Tests:        ~5 seconds
Integration Tests: ~20 seconds
Total Suite:       ~35 seconds
```

### Optimizing Test Speed

1. ✅ Use `@MockBean` instead of real beans where possible
2. ✅ Minimize `@SpringBootTest` usage (slow context loading)
3. ✅ Use H2 in-memory database for integration tests
4. ✅ Parallelize test execution (Maven Surefire)
5. ✅ Profile slow tests and optimize

## Conclusion

This comprehensive test suite ensures:

- ✅ All API endpoints work correctly
- ✅ Business logic is sound
- ✅ Database operations are reliable
- ✅ Complete user flows function end-to-end
- ✅ Edge cases and error scenarios handled
- ✅ Code coverage meets quality thresholds

**Next Steps:**

1. Review JaCoCo report regularly
2. Add tests for new features
3. Maintain >80% coverage for critical services
4. Monitor test execution time
5. Keep tests fast and reliable

---

**Testing Motto**: _"If it's not tested, it's broken."_
