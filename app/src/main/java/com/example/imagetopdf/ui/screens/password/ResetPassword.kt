package com.example.imagetopdf.ui.screens.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagetopdf.R
import com.example.imagetopdf.ui.components.CustomTextField
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.ReusableHeader
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary

@Composable
fun ResetPasswordScreen(){

    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmNewPasswordVisible by remember { mutableStateOf(false) }

    GradientBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /*-------Header-------*/
            ReusableHeader(
                icon = Icons.Filled.Key,
                iconModifier = Modifier.rotate(45f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            /*-------Main Content-------*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Forget Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Enter your new password below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                CustomTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    hint = "New Password",
                    icon = Icons.Filled.Lock,
                    isPasswordField = true,
                    passwordVisible = isNewPasswordVisible,
                    onVisibilityIconClick = {
                        isNewPasswordVisible = !isNewPasswordVisible
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    hint = "Confirm Password",
                    icon = Icons.Filled.Lock,
                    isPasswordField = true,
                    passwordVisible = isConfirmNewPasswordVisible,
                    onVisibilityIconClick = {
                        isConfirmNewPasswordVisible = !isConfirmNewPasswordVisible
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Logic: Check if newPassword == confirmPassword before submitting!
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Reset Password",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.resetpasswordillustration),
                    contentDescription = "Security Shield",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun ResetPasswordPreview(){
    MaterialTheme {
        ResetPasswordScreen()
    }
}