package works.jayesh.demo.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import works.jayesh.demo.auth.model.dto.*;
import works.jayesh.demo.auth.service.AuthService;
import works.jayesh.demo.common.model.ApiResponse;
import works.jayesh.demo.user.model.dto.UserRegistrationRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
 private final AuthService authService;
 // ============= Registration Endpoints =============
 @PostMapping("/register/password")
    public ResponseEntity<ApiResponse<AuthResponse>> registerWithPassword(
            @Valid @RequestBody UserRegistrationRequest request) {
        AuthResponse response = authService.registerWithPassword(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
 @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestRegistrationOtp(@Valid @RequestBody OtpRequest request) {
        authService.requestRegistrationOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email", null));
    }
 @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> registerWithOtp(
            @Valid @RequestBody RegisterWithOtpRequest request) {
        AuthResponse response = authService.registerWithOtp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully with OTP", response));
    }
 // ============= Login Endpoints =============
 @PostMapping("/login/password")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithPassword(
            @Valid @RequestBody LoginWithPasswordRequest request) {
        AuthResponse response = authService.loginWithPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
 @PostMapping("/login/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestLoginOtp(@Valid @RequestBody OtpRequest request) {
        authService.requestLoginOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email", null));
    }
 @PostMapping("/login/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithOtp(
            @Valid @RequestBody LoginWithOtpRequest request) {
        AuthResponse response = authService.loginWithOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful with OTP", response));
    }
 // ============= Token Refresh Endpoint =============
 @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
}
