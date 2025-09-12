package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "acciones_diarias")
data class AccionDiaria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val horaInicio: String, // formato "HH:mm"
    val horaFin: String,    // formato "HH:mm"
    val color: Int,         // código de color
    val diasSemana: String, // "Lunes,Martes,Miercoles..." o "Todos"
    val categoria: String,  // "Trabajo", "Estudio", "Ejercicio", etc.
    val prioridad: Int,     // 1-5
    val esPermanente: Boolean = true
)