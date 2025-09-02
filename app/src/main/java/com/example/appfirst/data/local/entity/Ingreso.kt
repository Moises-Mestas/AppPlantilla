package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverters
import com.example.appfirst.data.local.converters.DateConverter
import com.example.appfirst.data.local.entity.User

@Entity(
    tableName = "ingresos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,  // Referencia a la entidad User
            parentColumns = ["id"],  // Columna en User
            childColumns = ["userId"],  // Columna en Ingreso (la clave foránea)
            onDelete = ForeignKey.CASCADE  // Si se elimina un User, sus ingresos también se eliminan
        )
    ]
)
data class Ingreso(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monto: Double,  // Representa el monto con dos decimales
    val descripcion: String,
    val fecha: Long,  // Fecha almacenada como Long (milisegundos desde epoch)
    @field:androidx.room.ColumnInfo(name = "depositado_en")
    val depositadoEn: MedioPago,    val notas: String,
    val userId: Long, // ← FOREIGN KEY
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
