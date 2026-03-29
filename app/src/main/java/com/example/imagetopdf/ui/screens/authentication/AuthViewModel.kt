package com.example.imagetopdf.ui.screens.authentication

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetopdf.BuildConfig
import com.example.imagetopdf.network.SupabaseClient
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.util.UUID

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

    // ---- Email Auth ----

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
                android.util.Log.e("LOGIN_ERROR", "Login failed: ${e.message}", e)
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }

    // ---- Google Sign-In ----

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Step 1: Generate a secure nonce
                // The hashed version goes to Google, the raw version goes to Supabase
                val rawNonce = UUID.randomUUID().toString()
                val hashedNonce = hashNonce(rawNonce)

                // Step 2: Configure the Google ID option
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false) // Show all Google accounts, not just previously used ones
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setNonce(hashedNonce)
                    .build()

                // Step 3: Build the credential request
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Step 4: Launch the Google account picker
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                // Step 5: Extract the Google ID token from the result
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    // Step 6: Sign in to Supabase with the Google ID token
                    SupabaseClient.client.auth.signInWith(IDToken) {
                        idToken = googleIdTokenCredential.idToken
                        provider = Google
                        nonce = rawNonce
                    }

                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Google Sign-In failed. Please try again.")
                }

            }  catch (e: GetCredentialCancellationException) {
            _authState.value = AuthState.Idle

        } catch (e: androidx.credentials.exceptions.GetCredentialException) {
            // This catches ALL credential manager errors with specific types
            android.util.Log.e("GOOGLE_ERROR", "GetCredentialException type: ${e.type}", e)
            android.util.Log.e("GOOGLE_ERROR", "GetCredentialException message: ${e.message}", e)
            _authState.value = AuthState.Error(getCleanErrorMessage(e))

        } catch (e: Exception) {
                android.util.Log.e(
                    "GOOGLE_ERROR",
                    "General exception: ${e.javaClass.simpleName}: ${e.message}",
                    e
                )
                _authState.value = AuthState.Error(getCleanErrorMessage(e))
            }
        }
    }



    // SHA-256 hash the raw nonce so Google can verify it later
    private fun hashNonce(rawNonce: String): String {
        val bytes = rawNonce.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // ---- Password Reset ----

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

//    private fun getCleanErrorMessage(e: Exception): String {
//        val rawError = e.message ?: ""
//
//        return when {
//            rawError.contains("Invalid login credentials", ignoreCase = true) ->
//                "Invalid email or password."
//
//            rawError.contains("User already registered", ignoreCase = true) ->
//                "An account with this email already exists."
//
//            rawError.contains("Password should be at least", ignoreCase = true) ->
//                "Password must be at least 6 characters long."
//
//            rawError.contains("Unable to validate email address", ignoreCase = true) ->
//                "Please enter a valid, correctly formatted email."
//
//            rawError.contains("Token has expired or is invalid", ignoreCase = true) ||
//                    rawError.contains("invalid token", ignoreCase = true) ->
//                "Invalid or expired 6-digit code. Please try again."
//
//            rawError.contains("User not found", ignoreCase = true) ->
//                "No account found with this email address."
//
//            rawError.contains("rate limit", ignoreCase = true) ||
//                    rawError.contains("over_email_send_rate_limit", ignoreCase = true) ->
//                "Too many attempts. Please wait a moment and try again."
//
//            // Google-specific errors
//            rawError.contains("No credentials available", ignoreCase = true) ||
//                    rawError.contains("No Google accounts", ignoreCase = true) ->
//                "No Google account found on this device."
//
//            rawError.contains("play services", ignoreCase = true) ->
//                "Google Play Services is required for Google Sign-In."
//
//            else ->
//                "Something went wrong. Please check your internet connection and try again."
//        }
//    }

    private fun getCleanErrorMessage(e: Exception): String {
        val rawError = e.message ?: ""

        // ADD THIS LINE TEMPORARILY
        android.util.Log.e("AUTH_ERROR", "Raw error: $rawError", e)

        return when {
            rawError.contains("Invalid login credentials", ignoreCase = true) ->
                "Invalid email or password."
            // ... rest of your when block stays the same
            else ->
                "Something went wrong. Please check your internet connection and try again."
        }
    }
}