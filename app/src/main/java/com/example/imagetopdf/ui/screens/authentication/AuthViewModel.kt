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

    fun signUp(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Talk to Supabase
                SupabaseClient.client.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    fun login(emailInput: String,passwordInput: String){
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

    fun sendPasswordResetOtp(emailInput: String){
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

    fun verifyResetOtp(otpInput: String){
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

    private fun getCleanErrorMessage(e: Exception): String {
        val rawError = e.message ?: ""

        return when {
            rawError.contains("Invalid login credentials", ignoreCase = true) ->
                "Invalid email or password."

            rawError.contains("User already registered", ignoreCase = true) ->
                "An account with this email already exists."

            rawError.contains("Password should be at least", ignoreCase = true) ->
                "Password must be at least 6 characters long."

            rawError.contains("Unable to validate email address", ignoreCase = true) ->
                "Please enter a valid, correctly formatted email."

            rawError.contains("Email rate limit exceeded", ignoreCase = true) ->
                "Too many attempts. Please wait a moment and try again."

            else ->
                "Something went wrong. Please check your connection and try again."
        }
    }
}