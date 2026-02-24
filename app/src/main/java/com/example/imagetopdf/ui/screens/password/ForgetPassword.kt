package com.example.imagetopdf.ui.screens.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
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
import com.example.imagetopdf.ui.theme.TextSecondary

@Composable
fun ForgetPasswordScreen(){

    var email by remember { mutableStateOf("") }

    GradientBackground {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /*------Header------*/
            ReusableHeader(
                icon = Icons.Filled.Key,
                iconModifier = Modifier.rotate(45f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            /*-------Main Content-------*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter your email address and we'll send \nyou a link to reset your password.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Send", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Back to ", color = Color.Gray)
                    Text(
                        text = "Log In",
                        color = Color(0xFF5D5FEF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {  }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.emailillustration),
                    contentDescription = "Send Email Illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 50.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ForgetPasswordScreenPreview(){
    MaterialTheme {
        ForgetPasswordScreen()
    }
}