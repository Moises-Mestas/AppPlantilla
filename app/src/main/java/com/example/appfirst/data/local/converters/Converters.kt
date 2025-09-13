package com.example.appfirst.data.local.converters

import androidx.room.TypeConverter
import com.example.appfirst.data.local.entity.MedioPago

class Converters {
    @TypeConverter
    fun fromMedioPago(value: MedioPago?): String? = value?.name

    @TypeConverter
    fun toMedioPago(value: String?): MedioPago? =
        value?.let { MedioPago.valueOf(it) }
}