package com.example.appfirst.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.appfirst.data.local.dao.AccionDiariaDao
import com.example.appfirst.data.local.entity.AccionDiaria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class AccionDiariaRepository(private val dao: AccionDiariaDao) {
    suspend fun insert(accion: AccionDiaria) = dao.insertAccion(accion)

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

    suspend fun deleteAccionPorId(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                dao.deleteAccionPorId(id)
                Log.d("AccionDiariaRepo", "Acción eliminada por ID: $id")
            } catch (e: Exception) {
                Log.e("AccionDiariaRepo", "Error eliminando acción por ID: ${e.message}")
            }
        }
    }

    suspend fun delete(accion: AccionDiaria) = dao.deleteAccion(accion)
    suspend fun update(accion: AccionDiaria) = dao.updateAccion(accion)

    suspend fun obtenerAccionesDeHoy(): List<AccionDiaria> {
        val diaHoy = obtenerDiaDeLaSemanaHoy()
        return try {
            val todasAcciones = dao.getTodasAccionesSync()
            todasAcciones.filter { accion ->
                accion.diasSemana == "Todos" ||
                        accion.diasSemana.contains(diaHoy, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun obtenerDiaDeLaSemanaHoy(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }
    }

    suspend fun getAccionesFiltradasSync(
        texto: String,
        dia: String,
        categoria: String
    ): List<AccionDiaria> {
        return withContext(Dispatchers.IO) {
            try {
                // Necesitamos implementar este metodo en el DAO
                // Por ahora, filtramos manualmente
                val todasAcciones = dao.getTodasAccionesSync()
                todasAcciones.filter { accion ->
                    (texto.isEmpty() ||
                            accion.titulo.contains(texto, ignoreCase = true) ||
                            accion.descripcion.contains(texto, ignoreCase = true)) &&
                            (dia == "Todos" ||
                                    accion.diasSemana == "Todos" ||
                                    accion.diasSemana.contains(dia, ignoreCase = true)) &&
                            (categoria == "Todas" || accion.categoria == categoria)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}