package com.example.imagetopdf.ui.screens.conversion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagetopdf.R
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.theme.BrandBlueLight
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary

// ─────────────────────────────────────────────
// After Conversion Screen
// ─────────────────────────────────────────────

@Composable
fun AfterConversionScreen() {

    var selectedRating by remember { mutableIntStateOf(5) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // ── Scrollable body ──────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Header with overlapping green check circle ──
                SuccessHeader()

                Spacer(modifier = Modifier.height(20.dp))

                // ── Title ────────────────────────────────────────
                Text(
                    text = "PDF Created!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your PDF has been successfully created.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── PDF Summary Card ─────────────────────────────
                PdfSummaryCard()

                Spacer(modifier = Modifier.height(24.dp))

                // ── Rating Section ───────────────────────────────
                Text(
                    text = "Please rate your experience",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                StarRating(
                    currentRating = selectedRating,
                    onRatingChange = { selectedRating = it }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Bottom Nav ───────────────────────────────────────
            AfterScreenBottomBar()
        }
    }
}


// ─────────────────────────────────────────────
// Success Header  (gradient + overlapping green circle)
// ─────────────────────────────────────────────

@Composable
private fun SuccessHeader() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Gradient banner
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

        // Green check circle — overlaps the bottom of the banner
        Box(
            modifier = Modifier
                .padding(top = 90.dp)            // pushes circle down to overlap edge
                .size(90.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFDFF5E1)),  // light mint green
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Success",
                tint = Color(0xFF27AE60),         // solid green
                modifier = Modifier.size(48.dp)
            )
        }
    }
}


// ─────────────────────────────────────────────
// PDF Summary Card
// ─────────────────────────────────────────────

@Composable
private fun PdfSummaryCard() {
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
                .padding(16.dp)
        ) {

            // ── PDF file info row ─────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.pdficon),
                    contentDescription = "PDF Icon",
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Image Collection PDF",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1.2 MB, Today",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Spacer(modifier = Modifier.height(16.dp))

            // ── Image preview list ────────────────────────────
            ImagePreviewRow()
            Spacer(modifier = Modifier.height(12.dp))
            ImagePreviewRow()

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Spacer(modifier = Modifier.height(16.dp))

            // ── Action buttons row ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Download,
                    label = "Download\nPDF",
                    onClick = {}
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Share,
                    label = "Share\nPDF",
                    onClick = {}
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Folder,
                    label = "View PDF",
                    onClick = {}
                )
            }
        }
    }
}


// ─────────────────────────────────────────────
// Single image preview row  (thumbnail + placeholder lines)
// ─────────────────────────────────────────────

@Composable
private fun ImagePreviewRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .size(width = 110.dp, height = 75.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFCDD4E8))  // placeholder blue-gray
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Text placeholder lines
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PlaceholderLine(width = 140.dp)
            PlaceholderLine(width = 110.dp)
            PlaceholderLine(width = 90.dp)
        }
    }
}

@Composable
private fun PlaceholderLine(width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(width = width, height = 10.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFDDE3F0))
    )
}


// ─────────────────────────────────────────────
// Action Button  (icon + label inside a rounded card)
// ─────────────────────────────────────────────

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2FF)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BrandPurple,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}


// ─────────────────────────────────────────────
// Star Rating
// ─────────────────────────────────────────────

@Composable
private fun StarRating(
    currentRating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(5) { index ->
            val starNumber = index + 1
            Text(
                text = "★",
                fontSize = 36.sp,
                color = if (starNumber <= currentRating) Color(0xFFF2C94C) else Color(0xFFDDE3F0),
                modifier = Modifier
                    .padding(2.dp)
                    .let { mod ->
                        mod // clickable can be added here for interactivity
                    }
            )
        }
    }
}


// ─────────────────────────────────────────────
// Custom Bottom Bar  (Home · Settings · My Docs)
// Uses your existing BottomBarItem component style
// ─────────────────────────────────────────────

@Composable
private fun AfterScreenBottomBar() {
    androidx.compose.material3.Surface(
        color = Color.White,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.example.imagetopdf.ui.components.BottomBarItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = true,
                onClick = {}
            )
            com.example.imagetopdf.ui.components.BottomBarItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = false,
                onClick = {}
            )
            com.example.imagetopdf.ui.components.BottomBarItem(
                icon = Icons.Default.Folder,
                label = "My Docs",
                isSelected = false,
                onClick = {}
            )
        }
    }
}


// ─────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────

@Preview(showSystemUi = true)
@Composable
fun AfterConversionScreenPreview() {
    MaterialTheme {
        AfterConversionScreen()
    }
}