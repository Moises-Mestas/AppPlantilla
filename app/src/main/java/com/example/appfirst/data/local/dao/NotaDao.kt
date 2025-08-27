package com.example.appfirst.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appfirst.data.local.entity.Nota

@Dao
interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNota(nota: Nota)

    @Query("SELECT * FROM notas WHERE fecha = :fecha")
    suspend fun getNotasPorFecha(fecha: String): List<Nota>

    @Query("SELECT * FROM notas")
    suspend fun getTodasLasNotas(): List<Nota>

    @Delete
    suspend fun deleteNota(nota: Nota)

    @Update
    suspend fun updateNota(nota: Nota)
}
