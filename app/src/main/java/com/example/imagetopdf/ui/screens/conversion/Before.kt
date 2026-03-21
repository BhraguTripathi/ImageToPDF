package com.example.imagetopdf.ui.screens.conversion

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest // ✨ Needed for launch()
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // ✨ Added for layout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // ✨ Important for items(List)
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // ✨ Added for the Add Photos button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // ✨ Replaced standard Image with Coil
import com.example.imagetopdf.ui.components.CustomTextField
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.TopBar
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.OverlayBlack
import com.example.imagetopdf.ui.theme.TextPrimary

@Composable
fun BeforeConversionScreen(
    onCloseClick: () -> Unit,
    onConvertClick: () -> Unit,
    viewModel: PDFViewModel = viewModel()
) {

    val context = LocalContext.current

    val selectedImage by viewModel.selectedImages.collectAsState()

    val isConverting by viewModel.isConverting.collectAsState()

    BackHandler {
        viewModel.clearImages()
        viewModel.setPdfName("")
        onCloseClick()
    }

    LaunchedEffect(Unit) {
        viewModel.clearImages()
        viewModel.setPdfName("")
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 20)
    ) {
        uris : List<Uri> ->
        if(uris.isNotEmpty()){
            viewModel.addImage(uris)
        }
    }

    var pdfName by remember { mutableStateOf("") }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            TopBar(
                title = "Convert to PDF",
                buttonIcon = Icons.Default.Close,
                onButtonClicked = {
                    viewModel.clearImages()
                    viewModel.setPdfName("")
                    onCloseClick()
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                /* 2. Selected Images Info */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected Images (${selectedImage.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    TextButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text(text = "+ Add Photos", color = BrandPurple, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                /* 3. Grid */
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(selectedImage){
                        uri ->
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                        ){
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected Images",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(OverlayBlack)
                                    .clickable {
                                        viewModel.removeImage(uri)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Image",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                /* 4. PDF Name Input */
                // Using your CustomTextField component
                CustomTextField(
                    value = pdfName,
                    onValueChange = { pdfName = it },
                    hint = "Enter PDF Name",
                    icon = Icons.Default.Description
                )

                Spacer(modifier = Modifier.height(30.dp))

                /* 5. Convert Button */
                Button(
                    onClick = {
                        viewModel.setPdfName(pdfName)
                        viewModel.createPdf(context = context, onSuccess = {
                            onConvertClick()
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(50),
                    enabled = selectedImage.isNotEmpty() && pdfName.isNotEmpty() && !isConverting
                ) {
                    if(isConverting){
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Convert to PDF",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BeforePreview(){
    BeforeConversionScreen(
        onCloseClick = {},
        onConvertClick = {}
    )
}