package com.example.appfirst.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appfirst.data.local.dao.*
import com.example.appfirst.data.local.entity.*
import com.example.appfirst.data.local.converters.*  // 👈 ya importa todos los converters
// Si no usas el wildcard, añade:
// import com.example.appfirst.data.local.converters.MedioPagoConverter

@Database(
    entities = [
        User::class,
        Recordatorio::class,
        Examen::class,
        Tarea::class,
        Ingreso::class
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(
    FileListConverter::class,
    DateConverter::class,
    MedioPagoConverter::class,   // 👈 AÑADE ESTA LÍNEA
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordatorioDao(): RecordatorioDao
    abstract fun examenDao(): ExamenDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
