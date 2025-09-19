package com.example.appfirst.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,          // Solo nombre (no apellido)
    val email: String,         // Email para login
    val password: String,      // Contraseña
    val createdAt: Long = System.currentTimeMillis()
)