package com.example.appfirst.data.local.dao

import androidx.room.*
import com.example.appfirst.data.local.entity.Asignatura
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignaturaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asignatura: Asignatura): Long

    @Update
    suspend fun update(asignatura: Asignatura)

    @Delete
    suspend fun delete(asignatura: Asignatura)

    @Query("SELECT * FROM asignatura WHERE userId = :userId ORDER BY nombre ASC")
    fun getAsignaturasByUser(userId: Long): Flow<List<Asignatura>>

    @Query("SELECT * FROM asignatura WHERE id = :id AND userId = :userId")
    suspend fun getAsignaturaById(id: Long, userId: Long): Asignatura?

    @Query("SELECT * FROM asignatura WHERE userId = :userId AND nombre LIKE :query ORDER BY nombre ASC")
    fun searchAsignaturas(userId: Long, query: String): Flow<List<Asignatura>>

    @Query("DELETE FROM asignatura WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    // Verificar si ya existe una asignatura con el mismo nombre para el usuario
    @Query("SELECT COUNT(*) FROM asignatura WHERE userId = :userId AND nombre = :nombre")
    suspend fun existsAsignatura(userId: Long, nombre: String): Int

    // Obtener asignatura por nombre exacto
    @Query("SELECT * FROM asignatura WHERE userId = :userId AND nombre = :nombre")
    suspend fun getAsignaturaByNombre(userId: Long, nombre: String): Asignatura?
}