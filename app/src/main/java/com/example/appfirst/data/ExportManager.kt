package com.example.appfirst.data

import android.content.Context
import com.example.appfirst.data.local.entity.Ingreso
import java.io.File
import java.io.FileWriter

object ExportManager {

    fun exportIngresosToCSV(context: Context, ingresos: List<Ingreso>): File? {
        return try {
            val exportDir = File(context.getExternalFilesDir(null), "export")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, "ingresos.csv")
            val writer = FileWriter(file)

            // Encabezados (usa los campos reales de tu entidad)
            writer.append("ID,Monto,Descripcion,Fecha,DepositadoEn,Notas,UserId,CreatedAt,UpdatedAt\n")

            // Datos
            for (ingreso in ingresos) {
                writer.append("${ingreso.id},${ingreso.monto},${ingreso.descripcion},${ingreso.fecha},${ingreso.depositadoEn},${ingreso.notas},${ingreso.userId},${ingreso.createdAt},${ingreso.updatedAt}\n")
            }

            writer.flush()
            writer.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
