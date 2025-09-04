package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String, // Añadir título para identificar rápidamente
    val descripcion: String,
    val fecha: String, // formato "yyyy-MM-dd"
    val horaInicio: String, // formato "HH:mm"
    val horaFin: String,
    val color: Int, // código de color (0xFF2196F3)
    val tipo: String = "General", // "Evento", "Tarea", "Recordatorio"
    val horaRecordatorio: String? = null, // Nullable para eventos sin recordatorio
    val repeticion: String = "Ninguno", //"Diario", "Semanal", "Mensual", "Anual",
    val categoria: String = "Personal", // "Trabajo", "Personal", "Salud", etc.
    val prioridad: Int = 3 // 1-5 para prioridad

){
    // Función de ayuda para validar datos - DENTRO de la data class
    fun isValid(): Boolean {
        return titulo.isNotBlank() && fecha.isNotBlank() &&
                horaInicio.isNotBlank() && horaFin.isNotBlank()
    }
}