package com.example.imagetopdf.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){



    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            /*-----------Top App Bar------------*/
            TopBar(
                title = "Home",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = { /*TODO*/ }
            )

            /*----------Main Content----------*/
            Column(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


            }


            /*-----------Bottom App Bar-----------*/
            BottomBar()

        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}