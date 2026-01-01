package works.jayesh.demo.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final OtpService otpService;
    private final EmailService emailService;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    // Register with password
    public AuthResponse registerWithPassword(UserRegistrationRequest request) {
        log.info("Registering user with password: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());

        return generateAuthResponse(savedUser);
    }

    // Register with OTP
    public AuthResponse registerWithOtp(RegisterWithOtpRequest request) {
        log.info("Registering user with OTP: {}", request.getEmail());

        // Check if active user already exists
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (existingUser != null && existingUser.getStatus() == UserStatus.ACTIVE) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + request.getPhoneNumber());
        }

        // Inactive user should exist from requestRegistrationOtp
        if (existingUser == null) {
            throw new IllegalStateException("Please request OTP before registration");
        }

        // Verify OTP
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new IllegalStateException("Invalid or expired OTP");
        }

        // Update user details and activate
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        existingUser.setStatus(UserStatus.ACTIVE);
        existingUser.setEmailVerified(true);
        existingUser.setRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(existingUser);
        log.info("User registered with OTP successfully with ID: {}", savedUser.getId());

        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());

        return generateAuthResponse(savedUser);
    }

    // Login with password
    public AuthResponse loginWithPassword(LoginWithPasswordRequest request) {
        log.info("User logging in with password: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getEmail()));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", request.getEmail());

        return generateAuthResponse(user);
    }

    // Login with OTP
    public AuthResponse loginWithOtp(LoginWithOtpRequest request) {
        log.info("User logging in with OTP: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getEmail()));

        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new IllegalStateException("Invalid or expired OTP");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in with OTP successfully: {}", request.getEmail());

        return generateAuthResponse(user);
    }

    // Request OTP for login
    public void requestLoginOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        otpService.generateAndSendOtp(email);
        log.info("Login OTP requested for: {}", email);
    }

    // Request OTP for registration
    public void requestRegistrationOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered: " + email);
        }

        // Create temporary user for OTP verification
        User tempUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode("TEMP_PASSWORD"))
                .firstName("TEMP")
                .lastName("USER")
                .status(UserStatus.INACTIVE)
                .role(UserRole.CUSTOMER)
                .build();

        userRepository.save(tempUser);
        otpService.generateAndSendOtp(email);
        log.info("Registration OTP requested for: {}", email);
    }

    // Refresh access token
    public AuthResponse refreshAccessToken(String refreshToken) {
        log.info("Refreshing access token");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalStateException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalStateException("Refresh token mismatch");
        }

        log.info("Access token refreshed successfully for: {}", email);

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Save refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
