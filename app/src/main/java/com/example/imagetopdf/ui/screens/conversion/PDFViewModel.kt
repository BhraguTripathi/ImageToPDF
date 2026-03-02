package com.example.imagetopdf.ui.screens.conversion

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
}