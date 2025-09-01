package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.appfirst.data.local.converters.FileListConverter


@Entity(
    tableName = "recordatorios",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se borra usuario, se borran sus recordatorios
        )
    ]
)
data class Recordatorio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val fechaRecordatorio: Long,
    val color: String,
    val nota: String? = null,
    @TypeConverters(FileListConverter::class)
    val archivos: List<String> = emptyList(),
    val userId: Long, // ← FOREIGN KEY
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)