package com.example.appfirst.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.appfirst.data.local.dao.AccionDiariaDao
import com.example.appfirst.data.local.entity.AccionDiaria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AccionDiariaRepository(private val dao: AccionDiariaDao) {
    suspend fun insert(accion: AccionDiaria) = dao.insertAccion(accion)

    fun getTodas() = dao.getTodasAcciones()

    fun getPorDia(dia: String): LiveData<List<AccionDiaria>> {
        Log.d("AccionDiariaRepo", "Buscando acciones para día: $dia")
        return if (dia == "Todos") {
            dao.getTodasAcciones()
        } else {
            dao.getAccionesPorDia(dia)
        }
    }

    fun getAccionesFiltradas(texto: String, dia: String, categoria: String): LiveData<List<AccionDiaria>> {
        return if (texto.isEmpty() && dia == "Todos" && categoria == "Todas") {
            dao.getTodasAcciones()
        } else {
            dao.getAccionesFiltradas("%$texto%", dia, categoria)
        }
    }

    suspend fun getAccionPorId(id: Int): AccionDiaria? {
        return withContext(Dispatchers.IO) {
            try {
                dao.getAccionPorId(id)
            } catch (e: Exception) {
                Log.e("AccionDiariaRepo", "Error obteniendo acción por ID: ${e.message}")
                null
            }
        }
    }

    suspend fun delete(accion: AccionDiaria) = dao.deleteAccion(accion)
    suspend fun update(accion: AccionDiaria) = dao.updateAccion(accion)
}