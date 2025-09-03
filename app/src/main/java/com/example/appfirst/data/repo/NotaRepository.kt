package com.example.appfirst.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.appfirst.data.local.dao.NotaDao
import com.example.appfirst.data.local.entity.Nota

class NotaRepository(private val dao: NotaDao) {
    suspend fun insert(nota: Nota): Long = dao.insertNota(nota) // Devuelve Long

    //fun getPorFecha(fecha: String): LiveData<List<Nota>> = dao.getNotasPorFecha(fecha)

    fun getTodas(): LiveData<List<Nota>> = dao.getTodasLasNotas()

    fun getPorRangoFechas(startDate: String, endDate: String): LiveData<List<Nota>> =
        dao.getNotasPorRangoFechas(startDate, endDate)

    fun getPorFecha(fecha: String): LiveData<List<Nota>> {
        Log.d("NotaRepository", "Buscando notas para fecha: $fecha")
        return dao.getNotasPorFecha(fecha)
    }

    suspend fun delete(nota: Nota) = dao.deleteNota(nota)

    suspend fun update(nota: Nota) = dao.updateNota(nota)

    suspend fun getPorFechaDirecto(fecha: String): List<Nota> {
        Log.d("NotaRepository", "Buscando notas directo para: $fecha")
        val resultado = dao.getNotasPorFechaDirecto(fecha)
        Log.d("NotaRepository", "Encontradas ${resultado.size} notas")
        return resultado
    }
}