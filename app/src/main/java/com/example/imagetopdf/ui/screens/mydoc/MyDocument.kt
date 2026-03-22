package com.example.imagetopdf.ui.screens.mydoc

import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.imagetopdf.R
import com.example.imagetopdf.navigation.Screen
import com.example.imagetopdf.network.SupabaseClient
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.CustomSearchBar
import com.example.imagetopdf.ui.components.DocumentItemCard
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary
import io.github.jan.supabase.auth.auth
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyDocumentScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var savePdfs by remember { mutableStateOf<List<File>>(emptyList()) }

    // ---- NEW: track loading state ----
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: "guest"
        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val userDirectory = File(baseDir, userId)

        savePdfs = if (userDirectory.exists()) {
            userDirectory.listFiles { file ->
                file.extension.equals("pdf", ignoreCase = true)
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else {
            emptyList()
        }
        isLoading = false
    }

    val filteredPdfs = savePdfs.filter { file ->
        file.name.contains(searchText, ignoreCase = true)
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            TopBar(
                title = "My Documents",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = {
                    navController.navigate(Screen.Account.route)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                CustomSearchBar(
                    query = searchText,
                    onQueryChange = { newText -> searchText = newText }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---- Loading / Empty / List ----
                when {
                    isLoading -> {
                        // Spinner fills the remaining space and centers
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = BrandPurple,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Loading your documents...",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    filteredPdfs.isEmpty() -> {
                        EmptyState(
                            isSearching = searchText.isNotEmpty(),
                            searchQuery = searchText,
                            onCreateClick = {
                                navController.navigate(Screen.BeforeConversion.route)
                            }
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(filteredPdfs) { file ->
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
                                            context.startActivity(
                                                Intent.createChooser(viewIntent, "Open PDF")
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            BottomBar(navController = navController)
        }
    }
}

@Composable
private fun EmptyState(
    isSearching: Boolean,
    searchQuery: String,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.comingsoon),
            contentDescription = "No Documents",
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = if (isSearching) "No Results Found" else "No Documents Yet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (isSearching)
                "No PDFs match \"$searchQuery\".\nTry a different search term."
            else
                "You haven't created any PDFs yet.\nTap below to get started!",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        if (!isSearching) {
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Create Your First PDF",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}