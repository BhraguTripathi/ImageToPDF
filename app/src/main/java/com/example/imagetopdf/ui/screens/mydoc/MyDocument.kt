package com.example.imagetopdf.ui.screens.mydoc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.CustomSearchBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar

@Composable
fun MyDocumentScreen(){

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            /*-------Top Bar-------*/
            TopBar(
                title = "My Documents",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = {}
            )

            /*------Main Content-----*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                CustomSearchBar()

            }

            /*-------Bottom Bar------*/
            BottomBar()
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun MyDocumentScreenPreview(){
    MyDocumentScreen()
}