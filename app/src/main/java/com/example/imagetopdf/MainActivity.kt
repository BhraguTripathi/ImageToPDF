package com.example.imagetopdf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.imagetopdf.ui.screens.home.HomeScreen
import com.example.imagetopdf.ui.screens.mydoc.MyDocumentScreen
import com.example.imagetopdf.ui.theme.ImageToPDFTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageToPDFTheme {
                MyDocumentScreen()
            }
        }
    }
}
