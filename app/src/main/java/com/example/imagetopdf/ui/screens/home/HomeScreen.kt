package com.example.imagetopdf.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.imagetopdf.BuildConfig
import com.example.imagetopdf.R
import com.example.imagetopdf.navigation.Screen
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary
import com.example.imagetopdf.utils.UpdateChecker
import com.example.imagetopdf.utils.UpdateInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ---- Update state ----
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0) }
    var downloadId by remember { mutableStateOf(-1L) }
    var downloadFailed by remember { mutableStateOf(false) }

    // Check for update once when screen loads
    LaunchedEffect(Unit) {
        val info = UpdateChecker.checkForUpdate(BuildConfig.VERSION_NAME)
        if (info != null) {
            updateInfo = info
            showUpdateDialog = true
        }
    }

    // Poll download progress while downloading
    LaunchedEffect(isDownloading) {
        if (isDownloading && downloadId != -1L) {
            while (isDownloading) {
                delay(500)
                val progress = UpdateChecker.getProgress(context, downloadId)
                if (progress == -1) {
                    downloadFailed = true
                    isDownloading = false
                    break
                }
                downloadProgress = progress
                if (UpdateChecker.isComplete(context, downloadId)) {
                    isDownloading = false
                    UpdateChecker.installApk(context, downloadId)
                    break
                }
            }
        }
    }

    // ---- Update Dialog ----
    if (showUpdateDialog && updateInfo != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isDownloading) showUpdateDialog = false
            },
            containerColor = Color.White,
            title = {
                Text(
                    text = "Update Available 🎉",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Version ${updateInfo!!.latestVersion} is available.",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = updateInfo!!.updateMessage,
                        color = TextSecondary
                    )

                    // ---- Show progress bar while downloading ----
                    if (isDownloading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Downloading... $downloadProgress%",
                            color = BrandPurple,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = BrandPurple,
                            trackColor = Color(0xFFEEEEEE)
                        )
                    }

                    if (downloadFailed) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Download failed. Please try again.",
                            color = Color.Red
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isDownloading) {
                            downloadFailed = false
                            downloadProgress = 0
                            downloadId = UpdateChecker.startDownload(
                                context,
                                updateInfo!!.downloadUrl
                            )
                            isDownloading = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    enabled = !isDownloading
                ) {
                    Text(
                        text = if (isDownloading) "Downloading..." else "Download & Install",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                if (!isDownloading) {
                    TextButton(onClick = { showUpdateDialog = false }) {
                        Text(text = "Later", color = TextSecondary)
                    }
                }
            }
        )
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            TopBar(
                title = "Home",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = { navController.navigate(Screen.Account.route) }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.illustration__1),
                    contentDescription = "Illustration",
                    modifier = Modifier
                        .size(280.dp)
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Image to PDF Converter",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Convert your images into PDFs easily",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { navController.navigate(Screen.BeforeConversion.route) },
                    modifier = Modifier
                        .width(350.dp)
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Create New",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }

            BottomBar(navController = navController)
        }
    }
}