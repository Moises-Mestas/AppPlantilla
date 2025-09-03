package com.example.appfirst.data.local.entity

import androidx.room.ColumnInfo
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
    val color: Long, // código de color (0xFF2196F3)
    val tipo: String = "General", // "Evento", "Tarea", "Recordatorio"
    val horaRecordatorio: String?, // Nullable para eventos sin recordatorio
    val repeticion: String = "Ninguno", //"Diario", "Semanal", "Mensual", "Anual",
    val categoria: String, // "Trabajo", "Personal", "Salud", etc.
    val prioridad: Int // 1-5 para prioridad
)