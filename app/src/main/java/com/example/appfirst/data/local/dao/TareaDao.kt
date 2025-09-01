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

    @Query("SELECT * FROM tareas WHERE userId = :userId AND fechaEntrega BETWEEN :startDate AND :endDate ORDER BY fechaEntrega ASC")
    fun getTareasByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE userId = :userId AND asignatura = :asignatura ORDER BY fechaEntrega ASC")
    fun getTareasByAsignatura(userId: Long, asignatura: String): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE userId = :userId AND completada = :completada ORDER BY fechaEntrega ASC")
    fun getTareasByEstado(userId: Long, completada: Boolean): Flow<List<Tarea>>

    @Query("UPDATE tareas SET completada = :completada WHERE id = :id AND userId = :userId")
    suspend fun updateCompletada(id: Long, userId: Long, completada: Boolean)

    @Query("DELETE FROM tareas WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}