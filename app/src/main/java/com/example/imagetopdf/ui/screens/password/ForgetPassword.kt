package com.example.imagetopdf.ui.screens.password

import android.R.attr.icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.ReusableHeader

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
                icon = Icons.Filled.VpnKey,
                iconModifier = Modifier.rotate(-45f)
            )
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