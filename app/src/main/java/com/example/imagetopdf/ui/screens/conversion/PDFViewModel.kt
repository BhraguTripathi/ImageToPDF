package com.example.imagetopdf.ui.screens.conversion

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetopdf.utils.PdfConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PDFViewModel : ViewModel(){

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    //Add Image
    fun addImage(newImages: List<Uri>){
        val currentList = _selectedImages.value.toMutableList()
        currentList.addAll(newImages)
        _selectedImages.value = currentList
    }

    //Remove Image
    fun removeImage(imageUri: Uri){
        val currentList = _selectedImages.value.toMutableList()
        currentList.remove(imageUri)
        _selectedImages.value = currentList
    }

    //Clear Image
    fun clearImages(){
        _selectedImages.value = emptyList()
    }

    //Store PDF name
    private val _pdfName = MutableStateFlow<String>("")
    val pdfName: StateFlow<String> = _pdfName

    fun setPdfName(name: String){
        _pdfName.value = name
    }

    private val _isConverting = MutableStateFlow(false)
    val isConverting: StateFlow<Boolean> = _isConverting
    private val _createPdfFile = MutableStateFlow<File?>(null)
    val createPdfFile: StateFlow<File?> = _createPdfFile

    fun createPdf(context: Context, onSuccess: () -> Unit){
        val uris = _selectedImages.value
        val name = _pdfName.value

        if (uris.isEmpty() || name.isEmpty()) return

        viewModelScope.launch {
            _isConverting.value = true
            val resultFile = withContext(Dispatchers.IO){
                PdfConverter.convertImageToPdf(context, uris, name)
            }

            _isConverting.value = false
            if (resultFile!= null){
                _createPdfFile.value = resultFile
                onSuccess()
            } else {

            }
        }
    }

}