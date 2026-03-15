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
import androidx.compose.material.icons.filled.Star
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
import com.example.imagetopdf.ui.theme.ActionButtonBg
import com.example.imagetopdf.ui.theme.BrandBlueLight
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.DividerColor
import com.example.imagetopdf.ui.theme.StarActiveColor
import com.example.imagetopdf.ui.theme.StarInactiveColor
import com.example.imagetopdf.ui.theme.SuccessGreen
import com.example.imagetopdf.ui.theme.SuccessGreenBg
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
                .background(SuccessGreenBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Success",
                tint = SuccessGreen,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun PdfSummaryCard(
    pdfName: String,
    imageCount: Int,
    viewModel: PDFViewModel
) {
    val context = LocalContext.current
    val createdFile by viewModel.createPdfFile.collectAsState()
    val todayDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

    // ✅ Calculate real file size
    val fileSize = remember(createdFile) {
        val bytes = createdFile?.length() ?: 0L
        when {
            bytes >= 1_000_000 -> String.format("%.2f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            // ✅ PDF Icon + Name at the top
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.pdficon),
                    contentDescription = "PDF Icon",
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = pdfName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Vertical stats list
            StatRow(
                icon = "📄",
                label = "Total Pages",
                value = "$imageCount pages"
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatRow(
                icon = "💾",
                label = "File Size",
                value = fileSize
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatRow(
                icon = "📅",
                label = "Date Created",
                value = todayDate
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Action Buttons (unchanged)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Download,
                    label = "Download\nPDF",
                    onClick = {
                        createdFile?.let { privateFile ->
                            saveDocumentLauncher.launch(privateFile.name)
                        }
                    }
                )

                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Share,
                    label = "Share\nPDF",
                    onClick = {
                        createdFile?.let { file ->
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Share PDF using...")
                            )
                        }
                    }
                )

                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Folder,
                    label = "View PDF",
                    onClick = {
                        createdFile?.let { file ->
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
                                Intent.createChooser(viewIntent, "Open PDF with...")
                            )
                        }
                    }
                )
            }
        }
    }
}

// ✅ New reusable stat row composable
@Composable
private fun StatRow(
    icon: String,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ActionButton(modifier: Modifier = Modifier, icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(modifier = modifier.height(90.dp), onClick = onClick, colors = CardDefaults.cardColors(containerColor = ActionButtonBg), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
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
            Icon(
                imageVector = Icons.Filled.Star, // Assuming Star icon usage, though Text was used previously, I should probably stick to Text "★" if I can't find the Icon import. Wait, the previous code used Text.
                contentDescription = null,
                tint = if (starNumber <= currentRating) StarActiveColor else StarInactiveColor,
                modifier = Modifier.padding(2.dp).size(36.dp).clickable { onRatingChange(starNumber) }
            )
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