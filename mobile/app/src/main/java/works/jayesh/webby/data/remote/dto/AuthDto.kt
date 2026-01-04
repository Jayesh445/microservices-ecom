package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

// ========== Request DTOs ==========
data class LoginRequest(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String
)

data class RegisterRequest(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("phoneNumber") val phoneNumber: String?
)

data class OtpRequest(@SerializedName("email") val email: String)

data class LoginWithOtpRequest(
        @SerializedName("email") val email: String,
        @SerializedName("otp") val otp: String
)

data class RegisterWithOtpRequest(
        @SerializedName("email") val email: String,
        @SerializedName("otp") val otp: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("phoneNumber") val phoneNumber: String?
)

data class RefreshTokenRequest(@SerializedName("refreshToken") val refreshToken: String)

// ========== Response DTOs ==========
data class AuthResponse(
        @SerializedName("accessToken") val accessToken: String,
        @SerializedName("refreshToken") val refreshToken: String,
        @SerializedName("tokenType") val tokenType: String,
        @SerializedName("expiresIn") val expiresIn: Long,
        @SerializedName("user") val user: UserInfo
)

data class UserInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("email") val email: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("role") val role: String
)
