package com.example.appfirst.data.local.converters

import androidx.room.TypeConverter
import com.example.appfirst.data.local.entity.MedioPago

class MedioPagoConverter {
    @TypeConverter
    fun fromEnum(value: MedioPago?): String? = value?.name

    @TypeConverter
    fun toEnum(value: String?): MedioPago? =
        value?.let {
            // Soporta valores viejos en minúscula o con mayúscula inicial
            runCatching { MedioPago.valueOf(it.uppercase()) }.getOrNull()
        }
}
