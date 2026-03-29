package com.example.imagetopdf.ui.screens.account

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.imagetopdf.R
import com.example.imagetopdf.navigation.Screen
import com.example.imagetopdf.network.SupabaseClient
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.SettingItemCard
import com.example.imagetopdf.ui.components.TopBar
import com.example.imagetopdf.ui.theme.AccentOrange
import com.example.imagetopdf.ui.theme.BrandPurple
import com.example.imagetopdf.ui.theme.IconTint
import com.example.imagetopdf.ui.theme.SignOutRed
import com.example.imagetopdf.ui.theme.TextPrimary
import com.example.imagetopdf.ui.theme.TextSecondary
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun AccountScreen(navController: NavController) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }

    var showSignOutDialog by remember { mutableStateOf(false) }
    var isLoadingUser by remember { mutableStateOf(true) }
    var isUploadingPhoto by remember { mutableStateOf(false) }

    // ---- Load user info and profile picture ----
    LaunchedEffect(Unit) {
        isLoadingUser = true
        try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val email = user?.email ?: ""
            userEmail = email

            val rawName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content
            userName = if (!rawName.isNullOrBlank() && rawName != "null") {
                rawName
            } else {
                email.substringBefore("@").replaceFirstChar { it.uppercase() }
            }

            // Load avatar — works for both Google and custom uploaded photos
            // Google sign-in automatically sets avatar_url in metadata
            // Custom uploads also save their URL to avatar_url
            val metadataAvatar = user?.userMetadata?.get("avatar_url")?.jsonPrimitive?.content
            if (!metadataAvatar.isNullOrBlank() && metadataAvatar != "null") {
                avatarUrl = metadataAvatar
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoadingUser = false
    }

    // ---- Photo picker ----
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isUploadingPhoto = true
                try {
                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                        ?: throw Exception("User not logged in")

                    // Convert URI to ByteArray
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes == null || bytes.isEmpty()) {
                        throw Exception("Could not read image file")
                    }

                    // Upload to Supabase Storage bucket "avatars"
                    // Path: userId/profile.jpg — overwrites previous photo automatically
                    val filePath = "$userId/profile.jpg"
                    SupabaseClient.client.storage
                        .from("avatars")
                        .upload(filePath, bytes) {
                            upsert = true // Overwrite if exists
                        }

                    // Get the public URL of the uploaded image
                    val publicUrl = SupabaseClient.client.storage
                        .from("avatars")
                        .publicUrl(filePath)

                    // Save the URL to user metadata so it loads on next sign in
                    SupabaseClient.client.auth.updateUser {
                        data = buildJsonObject {
                            put("avatar_url", publicUrl)
                            // Preserve existing full_name
                            put("full_name", userName)
                        }
                    }

                    // Update UI immediately
                    avatarUrl = publicUrl

                    Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        "Failed to upload photo: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                isUploadingPhoto = false
            }
        }
    }

    // ---- Sign Out Dialog ----
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = Color.White,
            title = {
                Text(text = "Sign Out", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Text(text = "Are you sure you want to sign out?", color = TextPrimary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        scope.launch {
                            try {
                                SupabaseClient.client.auth.signOut()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SignOutRed)
                ) {
                    Text(text = "Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSignOutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
                ) {
                    Text(text = "Cancel", color = Color.White)
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
                title = "Account",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = {}
            )

            if (isLoadingUser) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                            text = "Loading profile...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // ---- Profile Picture ----
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.clickable(enabled = !isUploadingPhoto) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        // Profile image — shows Google photo, custom upload, or default
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(avatarUrl)
                                    .crossfade(true)
                                    // Force reload if URL changes after upload
                                    .memoryCacheKey(avatarUrl)
                                    .diskCacheKey(avatarUrl)
                                    .build(),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.user),
                                error = painterResource(R.drawable.user)
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.user),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Edit button or upload spinner
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(BrandPurple)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploadingPhoto) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Profile Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    SettingItemCard(
                        title = "Change Password",
                        icon = Icons.Filled.Lock,
                        iconTint = AccentOrange,
                        onClick = {
                            navController.navigate(Screen.ComingSoon.createRoute("Change Password"))
                        }
                    )

                    SettingItemCard(
                        title = "Notifications",
                        icon = Icons.Filled.Notifications,
                        iconTint = BrandPurple,
                        onClick = {
                            navController.navigate(Screen.ComingSoon.createRoute("Notifications"))
                        }
                    )

                    SettingItemCard(
                        title = "Appearance",
                        icon = Icons.Filled.Edit,
                        iconTint = BrandPurple,
                        onClick = {
                            navController.navigate(Screen.ComingSoon.createRoute("Appearance"))
                        }
                    )

                    SettingItemCard(
                        title = "Language",
                        icon = Icons.Filled.Language,
                        iconTint = IconTint,
                        onClick = {
                            navController.navigate(Screen.ComingSoon.createRoute("Language"))
                        }
                    )

                    SettingItemCard(
                        title = "Support",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        iconTint = IconTint,
                        onClick = {
                            navController.navigate(Screen.ComingSoon.createRoute("Support"))
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { showSignOutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SignOutRed),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Sign Out",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            BottomBar(navController = navController)
        }
    }
}