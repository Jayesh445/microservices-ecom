package works.jayesh.demo.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import works.jayesh.demo.user.model.dto.UserRegistrationRequest;
import works.jayesh.demo.user.model.dto.UserUpdateRequest;
import works.jayesh.demo.product.model.dto.ProductCreateRequest;
import works.jayesh.demo.address.model.dto.AddressRequest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for DTOs
 * Tests @Valid annotations and validation constraints
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("DTO Validation Tests")
class ValidationTest {

    @Autowired
    private Validator validator;

    // ==================== USER REGISTRATION VALIDATION TESTS ====================

    @Test
    @DisplayName("Should pass validation for valid user registration")
    void validateUserRegistration_Valid() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void validateUserRegistration_InvalidEmail() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation when email is null")
    void validateUserRegistration_NullEmail() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void validateUserRegistration_ShortPassword() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("123");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void validateUserRegistration_BlankFirstName() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("");
        request.setLastName("Doe");

        // When
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    // ==================== USER UPDATE VALIDATION TESTS ====================

    @Test
    @DisplayName("Should pass validation for valid user update")
    void validateUserUpdate_Valid() {
        // Given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");

        // When
        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow partial updates")
    void validateUserUpdate_PartialUpdate() {
        // Given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Jane"); // Only updating first name

        // When
        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    // ==================== PRODUCT VALIDATION TESTS ====================

    @Test
    @DisplayName("Should pass validation for valid product")
    void validateProduct_Valid() {
        // Given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setDescription("Test product description");
        request.setBrand("TestBrand");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(100);
        request.setCategoryId(1L);
        request.setSellerId(1L);

        // When
        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when product price is negative")
    void validateProduct_NegativePrice() {
        // Given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setPrice(new BigDecimal("-10.00"));
        request.setStockQuantity(100);
        request.setCategoryId(1L);
        request.setSellerId(1L);

        // When
        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when product name is blank")
    void validateProduct_BlankName() {
        // Given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("");
        request.setSku("TEST-001");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(100);

        // When
        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    // ==================== ADDRESS VALIDATION TESTS ====================

    @Test
    @DisplayName("Should pass validation for valid address")
    void validateAddress_Valid() {
        // Given
        AddressRequest request = new AddressRequest();
        request.setType(works.jayesh.demo.address.model.entity.AddressType.HOME);
        request.setFullName("John Doe");
        request.setPhoneNumber("+1234567890");
        request.setAddressLine1("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setCountry("USA");
        request.setPostalCode("10001");

        // When
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when required address fields are missing")
    void validateAddress_MissingFields() {
        // Given
        AddressRequest request = new AddressRequest();
        request.setFullName("John Doe");
        // Missing other required fields

        // When
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when postal code is invalid")
    void validateAddress_InvalidPostalCode() {
        // Given
        AddressRequest request = new AddressRequest();
        request.setFullName("John Doe");
        request.setPhoneNumber("+1234567890");
        request.setAddressLine1("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setCountry("USA");
        request.setPostalCode(""); // Empty postal code

        // When
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    // ==================== EMAIL VALIDATION TESTS ====================

    @Test
    @DisplayName("Should accept valid email formats")
    void validateEmail_ValidFormats() {
        String[] validEmails = {
                "user@example.com",
                "user.name@example.com",
                "user+tag@example.co.uk",
                "user_123@example-domain.com"
        };

        for (String email : validEmails) {
            UserRegistrationRequest request = new UserRegistrationRequest();
            request.setEmail(email);
            request.setPassword("Password123@");
            request.setFirstName("John");
            request.setLastName("Doe");

            Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(), "Should accept valid email: " + email);
        }
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void validateEmail_InvalidFormats() {
        String[] invalidEmails = {
                "plaintext",
                "@example.com",
                "user@",
                "user @example.com",
                "user..name@example.com"
        };

        for (String email : invalidEmails) {
            UserRegistrationRequest request = new UserRegistrationRequest();
            request.setEmail(email);
            request.setPassword("password123");
            request.setFirstName("John");
            request.setLastName("Doe");

            Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty(), "Should reject invalid email: " + email);
        }
    }
}