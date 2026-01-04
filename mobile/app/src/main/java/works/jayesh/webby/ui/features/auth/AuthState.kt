package works.jayesh.webby.ui.features.auth

data class LoginState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object LoginClicked : LoginEvent()
    object LoginWithOtpClicked : LoginEvent()
    object RegisterClicked : LoginEvent()
    object ForgotPasswordClicked : LoginEvent()
}

data class RegisterState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val phoneNumber: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val phoneError: String? = null
)

sealed class RegisterEvent {
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data class FirstNameChanged(val firstName: String) : RegisterEvent()
    data class LastNameChanged(val lastName: String) : RegisterEvent()
    data class PhoneChanged(val phone: String) : RegisterEvent()
    object RegisterClicked : RegisterEvent()
    object RegisterWithOtpClicked : RegisterEvent()
    object LoginClicked : RegisterEvent()
}

data class OtpState(
        val email: String = "",
        val otp: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val otpError: String? = null,
        val isResending: Boolean = false,
        val resendTimer: Int = 60
)

sealed class OtpEvent {
    data class OtpChanged(val otp: String) : OtpEvent()
    object VerifyClicked : OtpEvent()
    object ResendClicked : OtpEvent()
}
