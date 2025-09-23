package com.example.appfirst.data

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object BackupManager {

    fun backupDatabase(context: Context): File? {
        return try {
            // archivo de la DB
            val dbFile = context.getDatabasePath("app.db")

            // carpeta de backups
            val backupDir = File(context.getExternalFilesDir(null), "backup")
            if (!backupDir.exists()) backupDir.mkdirs()

            // archivo final
            val backupFile = File(backupDir, "app_backup.db")

            FileInputStream(dbFile).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }

            backupFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun restoreDatabase(context: Context, backupFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath("app.db")

            FileInputStream(backupFile).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
