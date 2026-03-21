package com.example.imagetopdf.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.URL

data class UpdateInfo(
    val latestVersion: String,
    val updateMessage: String,
    val downloadUrl: String
)

object UpdateChecker {

    private const val VERSION_JSON_URL =
        "https://raw.githubusercontent.com/BhraguTripathi/ImageToPDF/main/version.json"

    // ✅ 5 second timeout — if GitHub doesn't respond, silently skip
    suspend fun checkForUpdate(currentVersion: String): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(5000L) {
                try {
                    val json = URL(VERSION_JSON_URL).readText()
                    val obj = JSONObject(json)
                    val latest = obj.getString("latest_version")
                    val message = obj.getString("update_message")
                    val url = obj.getString("download_url")

                    if (isNewerVersion(latest, currentVersion)) {
                        UpdateInfo(latest, message, url)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        return try {
            val l = latest.split(".").map { it.toInt() }
            val c = current.split(".").map { it.toInt() }
            for (i in 0 until maxOf(l.size, c.size)) {
                val lv = l.getOrElse(i) { 0 }
                val cv = c.getOrElse(i) { 0 }
                if (lv > cv) return true
                if (lv < cv) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun startDownload(context: Context, downloadUrl: String): Long {
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle("ImageToPDF Update")
            setDescription("Downloading latest version...")
            setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "ImageToPDF-update.apk"
            )
            setMimeType("application/vnd.android.package-archive")
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.enqueue(request)
    }

    fun getProgress(context: Context, downloadId: Long): Int {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
        if (cursor.moveToFirst()) {
            val downloaded = cursor.getLong(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            )
            val total = cursor.getLong(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            )
            val status = cursor.getInt(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
            )
            cursor.close()
            if (status == DownloadManager.STATUS_FAILED) return -1
            return if (total > 0) ((downloaded * 100) / total).toInt() else 0
        }
        cursor.close()
        return 0
    }

    fun isComplete(context: Context, downloadId: Long): Boolean {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
            )
            cursor.close()
            return status == DownloadManager.STATUS_SUCCESSFUL
        }
        cursor.close()
        return false
    }

    fun installApk(context: Context, downloadId: Long) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = dm.getUriForDownloadedFile(downloadId)
        if (uri != null) {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}