package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descripcion: String,
    val fecha: String, // formato "dd/MM/yyyy"
    val color: String,
    val horaRecordatorio: String, // formato "HH:mm"
    val repeticion: String // "Ninguno", "Diario", etc.
)
