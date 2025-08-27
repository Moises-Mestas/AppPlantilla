package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.NotaDao
import com.example.appfirst.data.local.entity.Nota

class NotaRepository(private val dao: NotaDao) {
    suspend fun insert(nota: Nota) = dao.insertNota(nota)
    suspend fun getPorFecha(fecha: String) = dao.getNotasPorFecha(fecha)
    suspend fun getTodas() = dao.getTodasLasNotas()
    suspend fun delete(nota: Nota) = dao.deleteNota(nota)
    suspend fun update(nota: Nota) = dao.updateNota(nota)
}
