package com.example.appfirst.data

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {
    fun backupDatabase(context: Context): File? {
        return try {
            // Cambiar a Environment.DIRECTORY_DOWNLOADS
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val backupDir = File(downloadsDir, "AppFirst_Backups")

            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "app_backup_$timestamp.db")

            val currentDb = context.getDatabasePath("app.db")
            if (currentDb.exists()) {
                FileInputStream(currentDb).channel.use { input ->
                    FileOutputStream(backupFile).channel.use { output ->
                        input.transferTo(0, input.size(), output)
                    }
                }
                backupFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}