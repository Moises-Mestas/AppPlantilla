package com.example.appfirst.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

// Función para exportar la base de datos
// Función para exportar la base de datos
fun exportDatabase(context: Context) {
    try {
        // Obtiene la ruta del archivo de la base de datos
        val dbFile = File(context.getDatabasePath("app.db").absolutePath)
        val outputFile = File(context.getExternalFilesDir(null), "backup_${System.currentTimeMillis()}.db") // Generar nombre único

        // Copia el archivo de la base de datos a la ruta del archivo de respaldo
        dbFile.copyTo(outputFile, overwrite = true)

        // Guardar el historial de exportaciones con el nombre del archivo
        val prefs = context.getSharedPreferences("ExportHistory", Context.MODE_PRIVATE)
        val history = prefs.getStringSet("history", mutableSetOf())?.toMutableList() ?: mutableListOf()
        history.add(outputFile.name) // Guardamos solo el nombre del archivo
        prefs.edit().putStringSet("history", history.toSet()).apply()

        Log.d("Export", "Base de datos exportada a ${outputFile.absolutePath}")
    } catch (e: IOException) {
        Log.e("Export", "Error al exportar la base de datos", e)
    }
}



// Función para importar la base de datos
fun importDatabase(context: Context, fileName: String) {
    try {
        // Ruta del archivo de respaldo
        val inputFile = File(context.getExternalFilesDir(null), fileName) // Usamos el nombre de archivo pasado
        val outputFile = File(context.getDatabasePath("app.db").absolutePath)

        if (inputFile.exists()) {
            // Si el archivo de respaldo existe, copia el contenido a la base de datos
            inputFile.copyTo(outputFile, overwrite = true)
            Log.d("Import", "Base de datos importada desde ${inputFile.absolutePath}")
        } else {
            Log.e("Import", "El archivo de respaldo no existe.")
        }
    } catch (e: IOException) {
        Log.e("Import", "Error al importar la base de datos", e)
    }
}
