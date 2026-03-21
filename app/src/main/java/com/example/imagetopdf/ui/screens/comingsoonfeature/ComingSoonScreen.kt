package com.example.imagetopdf.ui.screens.comingsoonfeature

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.imagetopdf.R
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary

@Composable
fun ComingSoonScreen(
    featureName: String,
    navController: NavController
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {

            TopBar(
                title = featureName,
                buttonIcon = Icons.Filled.ArrowBack,
                onButtonClicked = { navController.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.comingsoon),
                    contentDescription = "Coming Soon Illustration",
                    modifier = Modifier
                        .size(260.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Coming Soon!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "We're working hard on the\n\"$featureName\" feature.",
                    fontSize = 15.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Stay tuned for the next update!",
                    fontSize = 13.sp,
                    color = BrandPurple,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Go Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}