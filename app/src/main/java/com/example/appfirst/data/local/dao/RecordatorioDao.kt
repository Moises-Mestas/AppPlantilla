package com.example.appfirst.data.local.dao

import androidx.room.*
import com.example.appfirst.data.local.entity.Recordatorio
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recordatorio: Recordatorio): Long

    @Update
    suspend fun update(recordatorio: Recordatorio)

    @Delete
    suspend fun delete(recordatorio: Recordatorio)

    @Query("SELECT * FROM recordatorios WHERE userId = :userId ORDER BY fechaRecordatorio ASC")
    fun getRecordatoriosByUser(userId: Long): Flow<List<Recordatorio>>

    @Query("SELECT * FROM recordatorios WHERE id = :id AND userId = :userId")
    suspend fun getRecordatorioById(id: Long, userId: Long): Recordatorio?

    @Query("SELECT * FROM recordatorios WHERE userId = :userId AND fechaRecordatorio BETWEEN :startDate AND :endDate ORDER BY fechaRecordatorio ASC")
    fun getRecordatoriosByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Recordatorio>>

    @Query("DELETE FROM recordatorios WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}