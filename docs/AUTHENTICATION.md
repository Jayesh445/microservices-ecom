# E-Commerce Application - JWT & OTP Authentication

## Overview

This application implements a comprehensive authentication system with dual login methods:

1. **Email + Password**: Traditional authentication
2. **Email + OTP**: One-Time Password sent via email

## Features

- ✅ JWT-based authentication with Access & Refresh tokens
- ✅ OTP-based email verification
- ✅ Dual registration methods (password or OTP)
- ✅ Dual login methods (password or OTP)
- ✅ Token refresh mechanism
- ✅ Email notifications for OTP and welcome messages

## Configuration

### 1. JWT Settings (`application.properties`)

```properties
# JWT Configuration
jwt.secret=MySecretKeyForJwtTokenGenerationAndValidationShouldBeAtLeast256BitsLongForHS256Algorithm
jwt.access-token-expiration=900000        # 15 minutes
jwt.refresh-token-expiration=604800000     # 7 days
```

### 2. Email Settings (Update with your credentials)

```properties
# Email Configuration (Gmail example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Important**: For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833) instead of your regular password.

## API Endpoints

### Registration

#### 1. Register with Password

```http
POST /api/auth/register/password
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890"
}
```

#### 2. Register with OTP (Step 1: Request OTP)

```http
POST /api/auth/register/request-otp
Content-Type: application/json

{
  "email": "user@example.com"
}
```

#### 3. Register with OTP (Step 2: Verify OTP)

```http
POST /api/auth/register/verify-otp
Content-Type: application/json

{
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "otp": "123456"
}
```

### Login

#### 1. Login with Password

```http
POST /api/auth/login/password
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

#### 2. Login with OTP (Step 1: Request OTP)

```http
POST /api/auth/login/request-otp
Content-Type: application/json

{
  "email": "user@example.com"
}
```

#### 3. Login with OTP (Step 2: Verify OTP)

```http
POST /api/auth/login/verify-otp
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "123456"
}
```

### Token Management

#### Refresh Access Token

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

## Response Format

### Successful Authentication Response

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "CUSTOMER"
    }
  }
}
```

### Using Access Token

Add the token to the Authorization header:

```http
GET /api/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

## Authentication Flow Diagrams

### Registration Flow with OTP

```
User → Request OTP → System creates temp user & sends OTP via email
User → Verify OTP with registration details → System activates user & returns tokens
```

### Login Flow with OTP

```
User → Request OTP → System sends OTP via email
User → Verify OTP → System returns access & refresh tokens
```

### Login Flow with Password

```
User → Submit email & password → System validates → Returns access & refresh tokens
```

### Token Refresh Flow

```
User → Submit refresh token → System validates → Returns new access token
```

## Security Features

1. **JWT Token Security**

   - Access tokens expire in 15 minutes
   - Refresh tokens expire in 7 days
   - Tokens signed with HS256 algorithm

2. **OTP Security**

   - 6-digit random OTP
   - Expires in 5 minutes
   - Single use only (cleared after verification)

3. **Password Security**

   - BCrypt password hashing
   - Minimum complexity requirements via validation

4. **Session Management**
   - Stateless JWT-based authentication
   - No server-side session storage

## Testing the Application

### 1. Start PostgreSQL

Ensure your PostgreSQL database is running with the database `ecommerce_db`.

### 2. Update Email Configuration

Update `application.properties` with your email credentials.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

### 4. Test Registration with OTP

```bash
# Step 1: Request OTP
curl -X POST http://localhost:8080/api/auth/register/request-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# Step 2: Check email for OTP, then verify
curl -X POST http://localhost:8080/api/auth/register/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "firstName":"John",
    "lastName":"Doe",
    "otp":"123456"
  }'
```

### 5. Test Login with Password

```bash
curl -X POST http://localhost:8080/api/auth/login/password \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "password":"YourPassword123"
  }'
```

### 6. Test Login with OTP

```bash
# Step 1: Request OTP
curl -X POST http://localhost:8080/api/auth/login/request-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# Step 2: Verify OTP
curl -X POST http://localhost:8080/api/auth/login/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "otp":"123456"
  }'
```

### 7. Test Token Refresh

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN"}'
```

## Project Structure

```
src/main/java/works/jayesh/demo/
├── auth/
│   ├── controller/
│   │   └── AuthController.java           # Authentication endpoints
│   ├── model/dto/
│   │   ├── AuthResponse.java             # Auth response with tokens
│   │   ├── LoginWithPasswordRequest.java
│   │   ├── LoginWithOtpRequest.java
│   │   ├── OtpRequest.java
│   │   ├── RegisterWithOtpRequest.java
│   │   └── RefreshTokenRequest.java
│   └── service/
│       ├── AuthService.java              # Authentication logic
│       ├── EmailService.java             # Email sending (async)
│       └── OtpService.java               # OTP generation & verification
├── security/
│   ├── jwt/
│   │   ├── JwtTokenProvider.java         # JWT token generation & validation
│   │   └── JwtAuthenticationFilter.java  # Request filter for JWT
│   └── service/
│       └── CustomUserDetailsService.java # User loading for authentication
├── config/
│   ├── SecurityConfig.java               # Spring Security configuration
│   └── AsyncConfig.java                  # Async email support
└── user/
    └── model/entity/
        └── User.java                     # User entity with OTP fields
```

## Troubleshooting

### Email Not Sending

1. Verify email credentials in `application.properties`
2. Enable "Less secure app access" or use App Password for Gmail
3. Check application logs for email errors

### OTP Not Working

1. Ensure OTP hasn't expired (5 minutes)
2. Check database for otp and otpExpiryTime values
3. Verify email service is working

### JWT Token Invalid

1. Check if token has expired
2. Verify JWT secret key is configured
3. Ensure token is sent with "Bearer " prefix

### Cannot Login

1. Ensure user status is ACTIVE
2. Check password is correct (case-sensitive)
3. Verify email exists in database

## Next Steps

- [ ] Add email templates with HTML
- [ ] Implement rate limiting for OTP requests
- [ ] Add account lockout after failed attempts
- [ ] Implement remember me functionality
- [ ] Add OAuth2 social login (Google, Facebook)
- [ ] Add two-factor authentication (2FA)
