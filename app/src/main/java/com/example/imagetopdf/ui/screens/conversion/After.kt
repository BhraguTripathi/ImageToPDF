package com.example.imagetopdf.ui.screens.conversion

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.imagetopdf.R
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.theme.BrandBlueLight
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AfterConversionScreen(
    navController: NavController,
    viewModel: PDFViewModel = viewModel()
) {
    var selectedRating by remember { mutableIntStateOf(5) }

    val selectedImages by viewModel.selectedImages.collectAsState()
    val pdfName by viewModel.pdfName.collectAsState()

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SuccessHeader()

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "PDF Created!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Your PDF has been successfully created.", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(24.dp))

                PdfSummaryCard(
                    pdfName = if (pdfName.isEmpty()) "Untitled.pdf" else if (pdfName.endsWith(".pdf")) pdfName else "$pdfName.pdf",
                    imageCount = selectedImages.size,
                    previewImages = selectedImages.take(2).map { it.toString() },
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Please rate your experience", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))
                StarRating(currentRating = selectedRating, onRatingChange = { selectedRating = it })
                Spacer(modifier = Modifier.height(24.dp))
            }

            BottomBar(navController = navController)
        }
    }
}

@Composable
private fun SuccessHeader() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandBlueLight, BrandPurple)
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
        )

        Box(
            modifier = Modifier
                .padding(top = 90.dp)
                .size(90.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFDFF5E1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Success",
                tint = Color(0xFF27AE60),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun PdfSummaryCard(
    pdfName: String,
    imageCount: Int,
    previewImages: List<String>,
    viewModel: PDFViewModel
) {
    val context = LocalContext.current
    val createdFile by viewModel.createPdfFile.collectAsState()
    val todayDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

    val saveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null && createdFile != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    createdFile!!.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.pdficon), contentDescription = "PDF Icon", modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = pdfName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$imageCount Images • $todayDate", fontSize = 13.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            previewImages.forEach { uriString ->
                ImagePreviewRow(uriString)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                // 1. DOWNLOAD BUTTON
                ActionButton(modifier = Modifier.weight(1f), icon = Icons.Filled.Download, label = "Download\nPDF", onClick = {
                    createdFile?.let { privateFile ->
                        saveDocumentLauncher.launch(privateFile.name)
                    }
                })

                // 2. SHARE BUTTON
                ActionButton(modifier = Modifier.weight(1f), icon = Icons.Filled.Share, label = "Share\nPDF", onClick = {
                    createdFile?.let { file ->
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share PDF using..."))
                    }
                })

                // 3. VIEW BUTTON
                ActionButton(modifier = Modifier.weight(1f), icon = Icons.Filled.Folder, label = "View PDF", onClick = {
                    createdFile?.let { file ->
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(viewIntent, "Open PDF with..."))
                    }
                })
            }
        }
    }
}

@Composable
private fun ImagePreviewRow(imageUriString: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUriString,
            contentDescription = "Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 110.dp, height = 75.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFCDD4E8))
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PlaceholderLine(width = 140.dp)
            PlaceholderLine(width = 110.dp)
            PlaceholderLine(width = 90.dp)
        }
    }
}

@Composable
private fun PlaceholderLine(width: androidx.compose.ui.unit.Dp) {
    Box(modifier = Modifier.size(width = width, height = 10.dp).clip(RoundedCornerShape(50)).background(Color(0xFFDDE3F0)))
}

@Composable
private fun ActionButton(modifier: Modifier = Modifier, icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(modifier = modifier.height(90.dp), onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2FF)), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = label, tint = BrandPurple, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun StarRating(currentRating: Int, onRatingChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(5) { index ->
            val starNumber = index + 1
            Text(text = "★", fontSize = 36.sp, color = if (starNumber <= currentRating) Color(0xFFF2C94C) else Color(0xFFDDE3F0), modifier = Modifier.padding(2.dp).clickable { onRatingChange(starNumber) })
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AfterPreview(){
    AfterConversionScreen(
        navController = NavController(LocalContext.current)
    )
}