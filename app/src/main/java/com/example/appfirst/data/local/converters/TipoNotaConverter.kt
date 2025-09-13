package com.example.appfirst.data.local.converters

import androidx.room.TypeConverter
import com.example.appfirst.data.local.entity.TipoNota

class TipoNotaConverter {
    @TypeConverter
    fun fromEnum(value: TipoNota?): String? = value?.name

    @TypeConverter
    fun toEnum(value: String?): TipoNota? =
        value?.let {
            runCatching { TipoNota.valueOf(it.uppercase()) }.getOrNull()
        }
}
