package com.example.imagetopdf.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object PdfConverter {

    fun convertImageToPdf(context: Context, imageUris: List<Uri>, pdfName: String): File?{

        val pdfDocument = PdfDocument()

        try {
            for ((index, uri) in imageUris.withIndex()){
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if(bitmap!=null){
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width,bitmap.height,index+1).create()
                    val page = pdfDocument.startPage(pageInfo)

                    val canvas = page.canvas
                    canvas.drawBitmap(bitmap,0f,0f,null)
                    pdfDocument.finishPage(page)
                }
            }
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val finalName = if (pdfName.endsWith(".pdf")) pdfName else "$pdfName.pdf"
            val file = File(downloadFolder, finalName)

            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()

            pdfDocument.close()

            return file
        }
        catch ( e: Exception ){
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }
}