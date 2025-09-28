package com.example.appfirst.data

import android.content.Context
import android.os.Environment
import com.example.appfirst.data.local.entity.Ingreso
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportManager {
    fun exportIngresosToCSV(context: Context, ingresos: List<Ingreso>): File? {
        return try {
            // Cambiar a Downloads público
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exportDir = File(downloadsDir, "AppFirst_Exports")

            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val csvFile = File(exportDir, "ingresos_$timestamp.csv")

            FileWriter(csvFile).use { writer ->
                // Escribir headers
                writer.write("ID,Fecha,Monto,Descripcion,Categoria\n")

                // Escribir datos
                ingresos.forEach { ingreso ->
                    writer.append("${ingreso.id},${ingreso.monto},${ingreso.descripcion},${ingreso.fecha},${ingreso.depositadoEn},${ingreso.notas},${ingreso.userId},${ingreso.createdAt},${ingreso.updatedAt}\n")
                }
            }
            csvFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}