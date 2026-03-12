package com.example.imagetopdf.ui.screens.mydoc

import android.content.Intent
import android.os.Environment
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.imagetopdf.navigation.Screen
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.CustomSearchBar
import com.example.imagetopdf.ui.components.DocumentItemCard
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyDocumentScreen(
    navController: NavController
){
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    var savePdfs by remember { mutableStateOf<List<File>>(emptyList()) }

    LaunchedEffect(Unit) {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (directory != null && directory.exists()){
            val files = directory.listFiles { file ->
                file.extension.equals("pdf", ignoreCase = true)
            }
            if (files != null ){
                savePdfs = files.sortedByDescending { it.lastModified() }
            }
        }
    }

    val filteredPdfs = savePdfs.filter { file ->
        file.name.contains(searchText, ignoreCase = true)
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

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
                onButtonClicked = {
                    navController.navigate(Screen.Account.route)
                }
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(filteredPdfs){
                    file ->
                        DocumentItemCard(
                            title = file.name,
                            date = dateFormat.format(Date(file.lastModified())),
                            onClick = {
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.provider",
                                        file
                                    )
                                    val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(viewIntent, "Open PDF"))
                                }
                                catch (e : Exception){
                                    e.printStackTrace()
                                }
                            }
                        )
                    }
                }
            }

            /*-------Bottom Bar------*/
            BottomBar(
                navController = navController
            )
        }
    }
}