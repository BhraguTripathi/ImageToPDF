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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.CustomSearchBar
import com.example.imagetopdf.ui.components.DocumentItemCard
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

                var searchText by remember { mutableStateOf("") }
                CustomSearchBar(
                    query=searchText,
                    onQueryChange = {
                        newText -> searchText = newText
                    }
                )

                Spacer(modifier =Modifier.height(24.dp))

                val dummyDocuments = listOf(
                    "Physics Assignment.pdf" to "Feb 21, 2026",
                    "Math Notes.pdf" to "Feb 20, 2026",
                    "Project Proposal.pdf" to "Feb 18, 2026",
                    "Resume_Final.pdf" to "Feb 15, 2026"
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dummyDocuments){
                    document ->
                        DocumentItemCard(
                            title = document.first,
                            date = document.second,
                            onClick = { }
                        )
                    }
                }
            }

            /*-------Bottom Bar------*/
            BottomBar()
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun MyDocumentScreenPreview(){
    MaterialTheme{
        MyDocumentScreen()
    }
}