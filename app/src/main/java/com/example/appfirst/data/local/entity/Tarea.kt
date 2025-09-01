package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.appfirst.data.local.converters.FileListConverter
import com.example.appfirst.data.local.entity.User
import androidx.room.ForeignKey


@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val fechaEntrega: Long,
    val fechaRecordatorio: Long,
    val asignatura: String,
    val nota: String? = null,
    @TypeConverters(FileListConverter::class)
    val archivos: List<String> = emptyList(),
    val completada: Boolean = false,
    val userId: Long, // ← FOREIGN KEY
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)