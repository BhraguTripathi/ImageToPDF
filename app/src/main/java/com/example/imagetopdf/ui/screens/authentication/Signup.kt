package com.example.imagetopdf.ui.screens.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagetopdf.ui.components.CustomTextField
import com.example.imagetopdf.ui.components.GoogleSignInButton
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.ReusableHeader
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary

@Composable
fun SignupScreen(
    onSignupClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignupClick()
            viewModel.resetState()
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ReusableHeader(icon = Icons.Filled.Person)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Create your account to get started.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    hint = "Full Name",
                    icon = Icons.Filled.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "Password",
                    icon = Icons.Default.Lock,
                    isPasswordField = true,
                    passwordVisible = passwordVisible,
                    onVisibilityIconClick = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (authState is AuthState.Error) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Email sign-up button
                Button(
                    onClick = {
                        if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.signUp(fullName, email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(BrandPurple),
                    shape = RoundedCornerShape(50),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Sign Up",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Already have an account? ", color = Color.Gray)
                    Text(
                        text = "Log In",
                        color = BrandPurple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TextSecondary
                    )
                    Text(
                        text = "or",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = TextSecondary
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Single Google sign-in button replacing the 3 social icons
                GoogleSignInButton(
                    onClick = { viewModel.signInWithGoogle(context) },
                    isLoading = authState is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}