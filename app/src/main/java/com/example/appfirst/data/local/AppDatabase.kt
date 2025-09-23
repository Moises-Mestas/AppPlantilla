package com.example.appfirst.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appfirst.data.local.dao.*
import com.example.appfirst.data.local.entity.*
import com.example.appfirst.data.local.converters.*

@Database(
    entities = [
        User::class,
        Nota::class,
        AccionDiaria::class,
        Asignatura::class,
        Recordatorio::class,
        Examen::class,
        Tarea::class,
        Ingreso::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(
    FileListConverter::class,
    DateConverter::class,
    MedioPagoConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun notaDao(): NotaDao
    abstract fun accionDiariaDao(): AccionDiariaDao
    abstract fun recordatorioDao(): RecordatorioDao
    abstract fun examenDao(): ExamenDao
    abstract fun tareaDao(): TareaDao
    abstract fun asignaturaDao(): AsignaturaDao
    abstract fun ingresoDao(): IngresoDao

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
