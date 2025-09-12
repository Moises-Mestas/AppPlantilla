package com.example.appfirst.data.local.dao

import androidx.room.*
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.data.local.entity.MedioPago
import com.example.appfirst.data.local.entity.TipoNota
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingreso: Ingreso): Long

    @Update
    suspend fun update(ingreso: Ingreso)

    @Delete
    suspend fun delete(ingreso: Ingreso)

    @Query("SELECT * FROM ingresos WHERE userId = :userId ORDER BY fecha ASC")
    fun getIngresosByUser(userId: Long): Flow<List<Ingreso>>

    @Query("SELECT * FROM ingresos WHERE id = :id AND userId = :userId")
    suspend fun getIngresoById(id: Int, userId: Long): Ingreso?

    @Query("SELECT * FROM ingresos WHERE userId = :userId AND fecha BETWEEN :startDate AND :endDate ORDER BY fecha ASC")
    fun getIngresosByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Ingreso>>

    @Query("SELECT * FROM ingresos WHERE userId = :userId AND depositado_en = :depositadoEn ORDER BY fecha DESC")
    fun getIngresosByDeposito(userId: Long, depositadoEn: MedioPago): Flow<List<Ingreso>>

    @Query("SELECT * FROM ingresos WHERE userId = :userId AND monto >= :monto ORDER BY fecha ASC")
    fun getIngresosByMonto(userId: Long, monto: Double): Flow<List<Ingreso>>

    @Query("DELETE FROM ingresos WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    @Query("SELECT SUM(monto) FROM ingresos WHERE userId = :userId")
    suspend fun getAllIngresosSum(userId: Long): Double
    @Query("SELECT * FROM ingresos WHERE userId = :userId AND notas = :notas ORDER BY fecha DESC")
    fun getIngresosByNotas(userId: Long, notas: TipoNota): Flow<List<Ingreso>>



}

