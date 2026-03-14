package com.example.imagetopdf.ui.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetopdf.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    var resetEmail: String = ""

    // ---- User Profile State ----
    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName

    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = SupabaseClient.client.auth.currentUserOrNull()
                val email = user?.email ?: ""
                _currentUserEmail.value = email

                val rawName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content
                _currentUserName.value = if (!rawName.isNullOrBlank() && rawName != "null") {
                    rawName
                } else {
                    email.substringBefore("@").replaceFirstChar { it.uppercase() }
                }
            } catch (e: Exception) {
                _currentUserName.value = "User"
                _currentUserEmail.value = ""
            }
        }
    }

    // ---- Auth Functions ----
    fun signUp(nameInput: String, emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                    data = buildJsonObject {
                        put("full_name", nameInput)
                    }
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    fun login(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    email = emailInput
                    password = passwordInput
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    fun sendPasswordResetOtp(emailInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                resetEmail = emailInput
                SupabaseClient.client.auth.resetPasswordForEmail(emailInput)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    fun verifyResetOtp(otpInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.verifyEmailOtp(
                    type = OtpType.Email.RECOVERY,
                    email = resetEmail,
                    token = otpInput
                )
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.updateUser {
                    password = newPassword
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // ---- Error Handling ----
    private fun getCleanErrorMessage(e: Exception): String {
        val rawError = e.message ?: ""

        return when {
            /* -------- LOGIN ERRORS -------- */
            rawError.contains("Invalid login credentials", ignoreCase = true) ->
                "Invalid email or password."

            /* -------- SIGNUP ERRORS -------- */
            rawError.contains("User already registered", ignoreCase = true) ->
                "An account with this email already exists."

            rawError.contains("Password should be at least", ignoreCase = true) ->
                "Password must be at least 6 characters long."

            rawError.contains("Unable to validate email address", ignoreCase = true) ->
                "Please enter a valid, correctly formatted email."

            /* -------- OTP & PASSWORD RESET ERRORS -------- */
            rawError.contains("Token has expired or is invalid", ignoreCase = true) ||
                    rawError.contains("invalid token", ignoreCase = true) ->
                "Invalid or expired 6-digit code. Please try again."

            rawError.contains("User not found", ignoreCase = true) ->
                "No account found with this email address."

            /* -------- RATE LIMITING -------- */
            rawError.contains("rate limit", ignoreCase = true) ||
                    rawError.contains("over_email_send_rate_limit", ignoreCase = true) ->
                "Too many attempts. Please wait a moment and try again."

            /* -------- FALLBACK -------- */
            else ->
                "Something went wrong. Please check your internet connection and try again."
        }
    }
}