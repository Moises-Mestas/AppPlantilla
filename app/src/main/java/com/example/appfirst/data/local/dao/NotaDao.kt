package com.example.appfirst.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appfirst.data.local.entity.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(nota: Nota): Long

    @Query("SELECT * FROM notas WHERE fecha = :fecha ORDER BY horaInicio ASC")
    fun obtenerNotasPorFecha(fecha: String): Flow<List<Nota>>

    @Query("SELECT * FROM notas ORDER BY fecha DESC, horaInicio ASC")
    fun obtenerTodasLasNotas(): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE id = :id")
    suspend fun obtenerNotaPorId(id: Int): Nota?

    @Query("SELECT * FROM notas WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha ASC, horaInicio ASC")
    fun obtenerNotasPorRango(fechaInicio: String, fechaFin: String): Flow<List<Nota>>

    @Update
    suspend fun actualizar(nota: Nota): Int

    @Delete
    suspend fun eliminar(nota: Nota): Int

    @Query("DELETE FROM notas WHERE id = :id")
    suspend fun eliminarPorId(id: Int): Int

    @Query("DELETE FROM notas WHERE fecha = :fecha")
    suspend fun eliminarNotasPorFecha(fecha: String): Int

    @Query("SELECT * FROM notas WHERE fecha = :fecha")
    suspend fun obtenerNotasPorFechaDirecto(fecha: String): List<Nota>

    @Query("SELECT * FROM notas WHERE fecha = :fecha ORDER BY horaInicio ASC")
    suspend fun obtenerNotasPorFechaSync(fecha: String): List<Nota>
}