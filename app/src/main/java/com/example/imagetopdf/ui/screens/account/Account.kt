package com.example.imagetopdf.ui.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagetopdf.R
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.SettingItemCard
import com.example.imagetopdf.ui.components.TopBar

@Composable
fun AccountScreen(){
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            /*------Top Bar-----*/
            TopBar(
                title = "Account",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = {}
            )

            /*-----Main Content------*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                //Profile Image
                Image(
                    painter = painterResource(R.drawable.avatar2),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dummy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "dummy@gmail.com",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(40.dp))

                //Setting Cards
                SettingItemCard(
                    title = "Change Password",
                    icon = Icons.Filled.Lock,
                    iconTint = Color(0xFFF2994A), // Orange
                    onClick = { /* Handle Click */ }
                )

                SettingItemCard(
                    title = "Notifications",
                    icon = Icons.Filled.Notifications,
                    iconTint = Color(0xFF5D5FEF), // Your Brand Purple
                    onClick = { /* Handle Click */ }
                )

                SettingItemCard(
                    title = "Appearance",
                    icon = Icons.Filled.Edit,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = { /* Handle Click */ }
                )

                SettingItemCard(
                    title = "Language",
                    icon = Icons.Filled.Language,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = { /* Handle Click */ }
                )

                SettingItemCard(
                    title = "Support",
                    icon = Icons.Filled.HelpOutline,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = { /* Handle Click */ }
                )
            }

            /*----Bottom Bar------*/
            BottomBar()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AccountScreenPreview(){
    MaterialTheme{
        AccountScreen()
    }
}