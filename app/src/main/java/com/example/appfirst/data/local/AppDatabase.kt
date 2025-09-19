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
    version = 5,
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
                    .addMigrations(MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE users_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            INSERT INTO users_new (id, name, email, password, createdAt)
            SELECT id, name, email, password, createdAt FROM users
            """.trimIndent()
        )

        database.execSQL("DROP TABLE users")

        database.execSQL("ALTER TABLE users_new RENAME TO users")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {

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