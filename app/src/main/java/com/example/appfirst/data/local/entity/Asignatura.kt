package com.example.appfirst.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.appfirst.data.local.converters.FileListConverter
import com.example.appfirst.data.local.entity.User
import androidx.room.ForeignKey


@Entity(tableName = "asignatura")
data class Asignatura(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val profesor: String,
    val aula: String,
    val userId: Long
)

