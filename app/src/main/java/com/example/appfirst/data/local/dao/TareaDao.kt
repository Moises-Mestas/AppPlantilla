package com.example.appfirst.data.local.dao

import androidx.room.*
import com.example.appfirst.data.local.entity.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarea: Tarea): Long

    @Update
    suspend fun update(tarea: Tarea)

    @Delete
    suspend fun delete(tarea: Tarea)

    @Query("SELECT * FROM tareas WHERE userId = :userId ORDER BY fechaEntrega ASC")
    fun getTareasByUser(userId: Long): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id AND userId = :userId")
    suspend fun getTareaById(id: Long, userId: Long): Tarea?

    // Cambiado: ahora filtra por asignaturaId en lugar de nombre
    @Query("SELECT * FROM tareas WHERE userId = :userId AND fechaEntrega BETWEEN :startDate AND :endDate ORDER BY fechaEntrega ASC")
    fun getTareasByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Tarea>>

    // Cambiado: ahora filtra por asignaturaId en lugar de string
    @Query("SELECT * FROM tareas WHERE userId = :userId AND asignaturaId = :asignaturaId ORDER BY fechaEntrega ASC")
    fun getTareasByAsignatura(userId: Long, asignaturaId: Long): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE userId = :userId AND completada = :completada ORDER BY fechaEntrega ASC")
    fun getTareasByEstado(userId: Long, completada: Boolean): Flow<List<Tarea>>

    @Query("UPDATE tareas SET completada = :completada WHERE id = :id AND userId = :userId")
    suspend fun updateCompletada(id: Long, userId: Long, completada: Boolean)

    @Query("DELETE FROM tareas WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    // NUEVAS CONSULTAS ÚTILES
    @Query("SELECT * FROM tareas WHERE asignaturaId = :asignaturaId ORDER BY fechaEntrega ASC")
    fun getTareasByAsignaturaId(asignaturaId: Long): Flow<List<Tarea>>

    // Obtener tareas pendientes próximas
    @Query("""
        SELECT * FROM tareas 
        WHERE userId = :userId AND completada = false AND fechaEntrega >= :startDate
        ORDER BY fechaEntrega ASC 
        LIMIT :limit
    """)
    fun getProximasTareasPendientes(userId: Long, startDate: Long, limit: Int = 10): Flow<List<Tarea>>
}