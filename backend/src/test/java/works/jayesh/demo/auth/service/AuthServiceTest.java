package works.jayesh.demo.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import works.jayesh.demo.auth.model.dto.*;
import works.jayesh.demo.common.exception.DuplicateResourceException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.security.jwt.JwtTokenProvider;
import works.jayesh.demo.security.service.CustomUserDetailsService;
import works.jayesh.demo.user.model.dto.UserRegistrationRequest;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.model.entity.UserRole;
import works.jayesh.demo.user.model.entity.UserStatus;
import works.jayesh.demo.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessTokenExpiration", 3600000L);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setPhoneNumber("+1234567890");
    }

    // ==================== REGISTRATION WITH PASSWORD TESTS ====================

    @Test
    @DisplayName("Should register user with password successfully")
    void registerWithPassword_Success() {
        // Given
        UserDetails userDetails = mock(UserDetails.class);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString());

        // When
        AuthResponse response = authService.registerWithPassword(registrationRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepository, times(2)).save(any(User.class)); // Called once in register, once in generateAuthResponse
        verify(emailService, times(1)).sendWelcomeEmail("test@example.com", "John");
    }

    @Test
    @DisplayName("Should throw exception when email already exists during registration")
    void registerWithPassword_DuplicateEmail() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            authService.registerWithPassword(registrationRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when phone already exists during registration")
    void registerWithPassword_DuplicatePhone() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            authService.registerWithPassword(registrationRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== REGISTRATION WITH OTP TESTS ====================

    @Test
    @DisplayName("Should register user with OTP successfully")
    void registerWithOtp_Success() {
        // Given
        RegisterWithOtpRequest otpRequest = new RegisterWithOtpRequest();
        otpRequest.setEmail("test@example.com");
        otpRequest.setOtp("123456");
        otpRequest.setFirstName("John");
        otpRequest.setLastName("Doe");
        otpRequest.setPhoneNumber("+1234567890");

        User inactiveUser = User.builder()
                .email("test@example.com")
                .status(UserStatus.INACTIVE)
                .build();

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(inactiveUser));
        when(otpService.verifyOtp(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        // When
        AuthResponse response = authService.registerWithOtp(otpRequest);

        // Then
        assertNotNull(response);
        verify(otpService, times(1)).verifyOtp("test@example.com", "123456");
    }

    @Test
    @DisplayName("Should throw exception when OTP not requested before registration")
    void registerWithOtp_NoOtpRequested() {
        // Given
        RegisterWithOtpRequest otpRequest = new RegisterWithOtpRequest();
        otpRequest.setEmail("test@example.com");
        otpRequest.setOtp("123456");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            authService.registerWithOtp(otpRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when invalid OTP provided")
    void registerWithOtp_InvalidOtp() {
        // Given
        RegisterWithOtpRequest otpRequest = new RegisterWithOtpRequest();
        otpRequest.setEmail("test@example.com");
        otpRequest.setOtp("wrong");

        User inactiveUser = User.builder()
                .email("test@example.com")
                .status(UserStatus.INACTIVE)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(inactiveUser));
        when(otpService.verifyOtp(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            authService.registerWithOtp(otpRequest);
        });
    }

    // ==================== LOGIN WITH PASSWORD TESTS ====================

    @Test
    @DisplayName("Should login with password successfully")
    void loginWithPassword_Success() {
        // Given
        LoginWithPasswordRequest loginRequest = new LoginWithPasswordRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        // When
        AuthResponse response = authService.loginWithPassword(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during login")
    void loginWithPassword_UserNotFound() {
        // Given
        LoginWithPasswordRequest loginRequest = new LoginWithPasswordRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.loginWithPassword(loginRequest);
        });
    }

    // ==================== LOGIN WITH OTP TESTS ====================

    @Test
    @DisplayName("Should login with OTP successfully")
    void loginWithOtp_Success() {
        // Given
        LoginWithOtpRequest otpLoginRequest = new LoginWithOtpRequest();
        otpLoginRequest.setEmail("test@example.com");
        otpLoginRequest.setOtp("123456");

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(otpService.verifyOtp(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        // When
        AuthResponse response = authService.loginWithOtp(otpLoginRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        verify(otpService, times(1)).verifyOtp("test@example.com", "123456");
    }

    @Test
    @DisplayName("Should throw exception when invalid OTP during login")
    void loginWithOtp_InvalidOtp() {
        // Given
        LoginWithOtpRequest otpLoginRequest = new LoginWithOtpRequest();
        otpLoginRequest.setEmail("test@example.com");
        otpLoginRequest.setOtp("wrong");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(otpService.verifyOtp(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            authService.loginWithOtp(otpLoginRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when user not found during OTP login")
    void loginWithOtp_UserNotFound() {
        // Given
        LoginWithOtpRequest otpLoginRequest = new LoginWithOtpRequest();
        otpLoginRequest.setEmail("nonexistent@example.com");
        otpLoginRequest.setOtp("123456");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.loginWithOtp(otpLoginRequest);
        });
    }

    // ==================== REFRESH TOKEN TESTS ====================

    @Test
    @DisplayName("Should refresh access token successfully")
    void refreshAccessToken_Success() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("validRefreshToken");

        UserDetails userDetails = mock(UserDetails.class);
        testUser.setRefreshToken("validRefreshToken");

        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.extractUsername(anyString())).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(any(UserDetails.class))).thenReturn("newAccessToken");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("newRefreshToken");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        AuthResponse response = authService.refreshAccessToken("validRefreshToken");

        // Then
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void refreshAccessToken_InvalidToken() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("invalidToken");

        when(jwtTokenProvider.validateToken(anyString())).thenReturn(false);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            authService.refreshAccessToken("invalidToken");
        });
    }
}
