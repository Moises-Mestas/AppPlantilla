package com.example.appfirst.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val lastname: String,
    val email: String,
    val age: Int,
    val phone: String ,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)