package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.NotaDao
import com.example.appfirst.data.local.entity.Nota
import kotlinx.coroutines.flow.Flow

class NotaRepository(private val notaDao: NotaDao) {
    suspend fun crearNota(nota: Nota): Long {
        if (!nota.isValid()) {
            throw IllegalArgumentException("Datos de nota inválidos")
        }
        return notaDao.insertar(nota)
    }

    suspend fun obtenerNotaPorId(id: Int): Nota? {
        return notaDao.obtenerNotaPorId(id)
    }

    suspend fun obtenerNotasPorFechaSync(fecha: String): List<Nota> {
        return notaDao.obtenerNotasPorFechaDirecto(fecha)
    }

    suspend fun actualizarNota(nota: Nota): Boolean {
        return notaDao.actualizar(nota) > 0
    }

    suspend fun eliminarNotaPorId(id: Int): Boolean {
        return notaDao.eliminarPorId(id) > 0
    }
}