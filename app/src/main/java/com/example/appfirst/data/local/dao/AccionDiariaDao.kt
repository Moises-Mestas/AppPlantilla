package com.example.appfirst.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appfirst.data.local.entity.AccionDiaria

@Dao
interface AccionDiariaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccion(accion: AccionDiaria)

    @Query("SELECT * FROM acciones_diarias ORDER BY horaInicio ASC")
    fun getTodasAcciones(): LiveData<List<AccionDiaria>>

    @Query("SELECT * FROM acciones_diarias WHERE diasSemana LIKE '%' || :dia || '%' OR diasSemana = 'Todos' ORDER BY horaInicio ASC")
    fun getAccionesPorDia(dia: String): LiveData<List<AccionDiaria>>

    @Query("""
        SELECT * FROM acciones_diarias 
        WHERE (:texto = '' OR titulo LIKE :texto OR descripcion LIKE :texto)
        AND (:dia = 'Todos' OR diasSemana = 'Todos' OR diasSemana LIKE '%' || :dia || '%')
        AND (:categoria = 'Todas' OR categoria = :categoria)
        ORDER BY horaInicio ASC
    """)
    fun getAccionesFiltradas(
        texto: String,
        dia: String,
        categoria: String
    ): LiveData<List<AccionDiaria>> // Cambiado a LiveData

    @Delete
    suspend fun deleteAccion(accion: AccionDiaria)

    @Update
    suspend fun updateAccion(accion: AccionDiaria)

    @Query("SELECT * FROM acciones_diarias WHERE id = :id")
    suspend fun getAccionPorId(id: Int): AccionDiaria?
}