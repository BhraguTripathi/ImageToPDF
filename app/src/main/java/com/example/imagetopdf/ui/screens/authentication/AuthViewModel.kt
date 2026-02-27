package com.example.imagetopdf.ui.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetopdf.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
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
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}