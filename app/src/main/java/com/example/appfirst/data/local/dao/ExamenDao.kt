package com.example.appfirst.data.local.dao

import androidx.room.*
import com.example.appfirst.data.local.entity.Examen
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(examen: Examen): Long

    @Update
    suspend fun update(examen: Examen)

    @Delete
    suspend fun delete(examen: Examen)

    @Query("SELECT * FROM examenes WHERE userId = :userId ORDER BY fechaExamen ASC")
    fun getExamenesByUser(userId: Long): Flow<List<Examen>>

    @Query("SELECT * FROM examenes WHERE id = :id AND userId = :userId")
    suspend fun getExamenById(id: Long, userId: Long): Examen?

    @Query("SELECT * FROM examenes WHERE userId = :userId AND fechaExamen BETWEEN :startDate AND :endDate ORDER BY fechaExamen ASC")
    fun getExamenesByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Examen>>

    @Query("SELECT * FROM examenes WHERE userId = :userId AND categoria = :categoria ORDER BY fechaExamen ASC")
    fun getExamenesByCategoria(userId: Long, categoria: String): Flow<List<Examen>>

    @Query("SELECT * FROM examenes WHERE userId = :userId AND asignatura = :asignatura ORDER BY fechaExamen ASC")
    fun getExamenesByAsignatura(userId: Long, asignatura: String): Flow<List<Examen>>

    @Query("DELETE FROM examenes WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}