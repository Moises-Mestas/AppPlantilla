package com.example.appfirst.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appfirst.data.local.dao.AccionDiariaDao
import com.example.appfirst.data.local.dao.NotaDao
import com.example.appfirst.data.local.dao.UserDao
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.data.local.entity.User

@Database(entities = [User::class, Nota::class, AccionDiaria::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun notaDao(): NotaDao
    abstract fun accionDiariaDao(): AccionDiariaDao

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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Crear tabla acciones_diarias
        database.execSQL("""
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
        """.trimIndent())
    }
}
