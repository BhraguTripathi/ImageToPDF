package com.example.imagetopdf.ui.screens.account

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.imagetopdf.R
import com.example.imagetopdf.navigation.Screen
import com.example.imagetopdf.network.SupabaseClient
import com.example.imagetopdf.ui.components.BottomBar
import com.example.imagetopdf.ui.components.GradientBackground
import com.example.imagetopdf.ui.components.SettingItemCard
import com.example.imagetopdf.ui.components.TopBar
import com.example.imagetopdf.ui.theme.BrandBlueLight
import com.example.imagetopdf.ui.theme.BrandPurple
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun AccountScreen(
    navController: NavController
){

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val sharedPreference = remember { context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE) }

    var profilePicKey by remember { mutableStateOf("profile_pic_uri_default") }

    var selectImageUri by remember { mutableStateOf<Uri?>(null) }

    var userName by remember { mutableStateOf("Loading...") }
    var userEmail by remember { mutableStateOf("Loading...") }

    var showSignOutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val email = user?.email ?: "Unknown Email"
        userEmail = email

        val rawName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content
        if(!rawName.isNullOrBlank() && rawName != "null"){
            userName = rawName
        } else {
            userName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        }

        profilePicKey = "profile_pic_uri_${email}"
        val savedUri = sharedPreference.getString(profilePicKey, null)
        if (savedUri != null) {
            selectImageUri = Uri.parse(savedUri)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {uri ->
        if (uri!=null){
            try {
                // Tell Android we want permanent permission to look at this photo
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectImageUri = uri
            sharedPreference.edit().putString(profilePicKey, uri.toString()).apply()
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = Color.White,
            title = {
                Text(text = "Sign Out", fontWeight = FontWeight.Bold, color = Color.Black)
            },
            text = {
                Text(text = "Are you sure you want to sign out?", color = Color.Black)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        scope.launch {
                            try {
                                SupabaseClient.client.auth.signOut()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B))
                ) {
                    Text(text = "Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSignOutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D5FEF))
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
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            /*------Top Bar-----*/
            TopBar(
                title = "Account",
                buttonIcon = Icons.Filled.Person,
                onButtonClicked = {}
            )

            /*-----Main Content------*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                //Profile Image
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ){
                    if (selectImageUri!=null){
                        AsyncImage(
                            model = selectImageUri,
                            contentDescription = "Profile_Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
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

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5D5FEF))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(40.dp))

                //Setting Cards
                SettingItemCard(
                    title = "Change Password",
                    icon = Icons.Filled.Lock,
                    iconTint = Color(0xFFF2994A),
                    onClick = {
                        navController.navigate(Screen.ComingSoon.createRoute("Change Password"))
                    }
                )

                SettingItemCard(
                    title = "Notifications",
                    icon = Icons.Filled.Notifications,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = {
                        navController.navigate(Screen.ComingSoon.createRoute("Notifications"))
                    }
                )

                SettingItemCard(
                    title = "Appearance",
                    icon = Icons.Filled.Edit,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = {
                        navController.navigate(Screen.ComingSoon.createRoute("Appearance"))
                    }
                )

                SettingItemCard(
                    title = "Language",
                    icon = Icons.Filled.Language,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = {
                        navController.navigate(Screen.ComingSoon.createRoute("Language"))
                    }
                )

                SettingItemCard(
                    title = "Support",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    iconTint = Color(0xFF5D5FEF),
                    onClick = {
                        navController.navigate(Screen.ComingSoon.createRoute("Support"))
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        showSignOutDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B)), // Red color for Sign Out
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

            /*----Bottom Bar------*/
            BottomBar(
                navController = navController
            )
        }
    }
}