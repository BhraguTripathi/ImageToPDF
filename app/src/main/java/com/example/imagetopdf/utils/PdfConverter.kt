package com.example.imagetopdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object PdfConverter {

    fun convertImageToPdf(context: Context, imageUris: List<Uri>, pdfName: String, userId: String): File?{

        val pdfDocument = PdfDocument()

        try {
            for ((index, uri) in imageUris.withIndex()){
                val inputStream = context.contentResolver.openInputStream(uri)
                var bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if(bitmap!=null){
                    val maxWidth=1000
                    if(bitmap.width > maxWidth){
                        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        val newHeight = (maxWidth/ratio).toInt()
                        bitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
                    }

                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width,bitmap.height,index+1).create()
                    val page = pdfDocument.startPage(pageInfo)

                    val canvas = page.canvas
                    canvas.drawBitmap(bitmap,0f,0f,null)
                    pdfDocument.finishPage(page)
                }
            }

            val bashFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val userFolder = File(bashFolder, userId)
            if (!userFolder.exists()) {
                userFolder.mkdirs()
            }

            val finalName = if (pdfName.endsWith(".pdf")) pdfName else "$pdfName.pdf"
            val file = File(userFolder, finalName)

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