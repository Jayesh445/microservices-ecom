package works.jayesh.demo.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.auth.model.dto.*;
import works.jayesh.demo.auth.service.AuthService;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.user.model.dto.UserRegistrationRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for AuthController
 * Tests REST API contracts: HTTP status codes, validation, response structure
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController API Tests")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==================== REGISTRATION TESTS ====================

    @Test
    @DisplayName("POST /api/auth/register/password - Should return 201 with auth response")
    void registerWithPassword_Success() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");

        when(authService.registerWithPassword(any(UserRegistrationRequest.class)))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));

        verify(authService, times(1)).registerWithPassword(any(UserRegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register/password - Should return 400 for invalid email")
    void registerWithPassword_InvalidEmail() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("invalid-email");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When & Then
        mockMvc.perform(post("/api/auth/register/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerWithPassword(any());
    }

    @Test
    @DisplayName("POST /api/auth/register/password - Should return 400 for weak password")
    void registerWithPassword_WeakPassword() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("weak");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When & Then
        mockMvc.perform(post("/api/auth/register/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerWithPassword(any());
    }

    @Test
    @DisplayName("POST /api/auth/register/password - Should return 400 for missing required fields")
    void registerWithPassword_MissingFields() throws Exception {
        // Given - Empty request
        UserRegistrationRequest request = new UserRegistrationRequest();

        // When & Then
        mockMvc.perform(post("/api/auth/register/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerWithPassword(any());
    }

    @Test
    @DisplayName("POST /api/auth/register/request-otp - Should return 200 and send OTP")
    void requestRegistrationOtp_Success() throws Exception {
        // Given
        OtpRequest request = new OtpRequest();
        request.setEmail("test@example.com");

        doNothing().when(authService).requestRegistrationOtp(anyString());

        // When & Then
        mockMvc.perform(post("/api/auth/register/request-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP sent to your email"));

        verify(authService, times(1)).requestRegistrationOtp("test@example.com");
    }

    @Test
    @DisplayName("POST /api/auth/register/verify-otp - Should return 201 with auth response")
    void registerWithOtp_Success() throws Exception {
        // Given
        RegisterWithOtpRequest request = new RegisterWithOtpRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");
        request.setFirstName("John");
        request.setLastName("Doe");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");

        when(authService.registerWithOtp(any(RegisterWithOtpRequest.class)))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));

        verify(authService, times(1)).registerWithOtp(any(RegisterWithOtpRequest.class));
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("POST /api/auth/login/password - Should return 200 with auth response")
    void loginWithPassword_Success() throws Exception {
        // Given
        LoginWithPasswordRequest request = new LoginWithPasswordRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123@");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");

        when(authService.loginWithPassword(any(LoginWithPasswordRequest.class)))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));

        verify(authService, times(1)).loginWithPassword(any(LoginWithPasswordRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login/password - Should return 400 for missing credentials")
    void loginWithPassword_MissingCredentials() throws Exception {
        // Given - Empty request
        LoginWithPasswordRequest request = new LoginWithPasswordRequest();

        // When & Then
        mockMvc.perform(post("/api/auth/login/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).loginWithPassword(any());
    }

    @Test
    @DisplayName("POST /api/auth/login/request-otp - Should return 200")
    void requestLoginOtp_Success() throws Exception {
        // Given
        OtpRequest request = new OtpRequest();
        request.setEmail("test@example.com");

        doNothing().when(authService).requestLoginOtp(anyString());

        // When & Then
        mockMvc.perform(post("/api/auth/login/request-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP sent to your email"));

        verify(authService, times(1)).requestLoginOtp("test@example.com");
    }

    @Test
    @DisplayName("POST /api/auth/login/verify-otp - Should return 200 with auth response")
    void loginWithOtp_Success() throws Exception {
        // Given
        LoginWithOtpRequest request = new LoginWithOtpRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");

        when(authService.loginWithOtp(any(LoginWithOtpRequest.class)))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));

        verify(authService, times(1)).loginWithOtp(any(LoginWithOtpRequest.class));
    }

    // ==================== TOKEN REFRESH TESTS ====================

    @Test
    @DisplayName("POST /api/auth/refresh - Should return 200 with new access token")
    void refreshToken_Success() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("new-access-token");
        authResponse.setRefreshToken("valid-refresh-token");

        when(authService.refreshAccessToken(anyString()))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));

        verify(authService, times(1)).refreshAccessToken("valid-refresh-token");
    }

    @Test
    @DisplayName("POST /api/auth/refresh - Should return 400 for missing refresh token")
    void refreshToken_MissingToken() throws Exception {
        // Given - Empty request
        RefreshTokenRequest request = new RefreshTokenRequest();

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).refreshAccessToken(any());
    }

    @Test
    @DisplayName("POST /api/auth/register/password - Should handle malformed JSON")
    void registerWithPassword_MalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerWithPassword(any());
    }
}
