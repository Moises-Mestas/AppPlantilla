package com.example.appfirst.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appfirst.data.local.dao.*
import com.example.appfirst.data.local.entity.*
import com.example.appfirst.data.local.converters.*


@Database(
    entities = [
        User::class,
        Recordatorio::class,
        Examen::class,
        Tarea::class ,
        Ingreso::class

    ],
    version = 3,
    exportSchema = false
)

@TypeConverters(
    FileListConverter::class,
    DateConverter::class,
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordatorioDao(): RecordatorioDao
    abstract fun examenDao(): ExamenDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}