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
        Ingreso::class   // ✅ lo traemos de HEAD
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    FileListConverter::class,
    DateConverter::class,
    MedioPagoConverter::class   // ✅ también de HEAD
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun notaDao(): NotaDao
    abstract fun accionDiariaDao(): AccionDiariaDao
    abstract fun recordatorioDao(): RecordatorioDao
    abstract fun examenDao(): ExamenDao
    abstract fun tareaDao(): TareaDao
    abstract fun asignaturaDao(): AsignaturaDao
    abstract fun ingresoDao(): IngresoDao   // ✅ traído de HEAD

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

// Migración de la versión 3 a la 4
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Crear tabla acciones_diarias
        database.execSQL(
            """
            CREATE TABLE acciones_diarias (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                titulo TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                horaInicio TEXT NOT NULL,
                horaFin TEXT NOT NULL,
                color INTEGER NOT NULL,
                diasSemana TEXT NOT NULL,
                categoria TEXT NOT NULL,
                prioridad INTEGER NOT NULL,
                esPermanente INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
        )
    }
}
