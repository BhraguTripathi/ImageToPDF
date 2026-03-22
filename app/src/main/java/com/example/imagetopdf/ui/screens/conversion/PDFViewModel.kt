package com.example.imagetopdf.ui.screens.conversion

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetopdf.network.SupabaseClient
import com.example.imagetopdf.utils.PdfConverter
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// ---- Conversion result sealed class ----
sealed class ConversionState {
    object Idle : ConversionState()
    object Converting : ConversionState()
    object Success : ConversionState()
    data class Error(val message: String) : ConversionState()
}

class PDFViewModel : ViewModel() {

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    fun addImage(newImages: List<Uri>) {
        val currentList = _selectedImages.value.toMutableList()
        currentList.addAll(newImages)
        _selectedImages.value = currentList
    }

    fun removeImage(imageUri: Uri) {
        val currentList = _selectedImages.value.toMutableList()
        currentList.remove(imageUri)
        _selectedImages.value = currentList
    }

    fun clearImages() {
        _selectedImages.value = emptyList()
    }

    fun clearAll() {
        _selectedImages.value = emptyList()
        _pdfName.value = ""
        _conversionState.value = ConversionState.Idle
        _createPdfFile.value = null
    }

    private val _pdfName = MutableStateFlow("")
    val pdfName: StateFlow<String> = _pdfName

    fun setPdfName(name: String) {
        _pdfName.value = name
    }

    // ---- Replaced _isConverting with full ConversionState ----
    private val _conversionState = MutableStateFlow<ConversionState>(ConversionState.Idle)
    val conversionState: StateFlow<ConversionState> = _conversionState

    // ---- Keep isConverting as a derived helper for the button enabled state ----
    val isConverting: Boolean
        get() = _conversionState.value is ConversionState.Converting

    private val _createPdfFile = MutableStateFlow<File?>(null)
    val createPdfFile: StateFlow<File?> = _createPdfFile

    fun resetConversionState() {
        _conversionState.value = ConversionState.Idle
    }

    fun createPdf(context: Context, onSuccess: () -> Unit) {
        val uris = _selectedImages.value
        val name = _pdfName.value

        if (uris.isEmpty()) {
            _conversionState.value = ConversionState.Error("Please select at least one image.")
            return
        }
        if (name.isBlank()) {
            _conversionState.value = ConversionState.Error("Please enter a name for your PDF.")
            return
        }

        viewModelScope.launch {
            _conversionState.value = ConversionState.Converting

            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: "guest"

                val resultFile = withContext(Dispatchers.IO) {
                    PdfConverter.convertImageToPdf(context, uris, name, userId)
                }

                if (resultFile != null) {
                    _createPdfFile.value = resultFile
                    _conversionState.value = ConversionState.Success
                    onSuccess()
                } else {
                    // ---- Conversion returned null ----
                    _conversionState.value = ConversionState.Error(
                        "Failed to create PDF. Please try again."
                    )
                }
            } catch (e: Exception) {
                // ---- Unexpected crash during conversion ----
                _conversionState.value = ConversionState.Error(
                    "Something went wrong: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }
}