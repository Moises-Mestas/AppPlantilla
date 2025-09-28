package com.example.appfirst.data.repo

import android.content.Context
import com.example.appfirst.data.local.dao.NotaDao
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.utils.AlarmHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaRepository(private val notaDao: NotaDao) {
    private var alarmHelper: AlarmHelper? = null
    fun setAlarmHelper(context: Context) {
        alarmHelper = AlarmHelper(context)
    }
    suspend fun crearNota(nota: Nota): Long {
        if (!nota.isValid()) {
            throw IllegalArgumentException("Datos de nota inválidos")
        }
        val id = notaDao.insertar(nota)

        // Programar alarma si la nota tiene hora de inicio
        alarmHelper?.let { helper ->
            val notaConId = nota.copy(id = id.toInt())
            helper.programarAlarma(notaConId)
        }

        return id
    }

    suspend fun actualizarNota(nota: Nota): Boolean {
        // Cancelar alarma existente
        alarmHelper?.cancelarAlarma(nota.id)

        val result = notaDao.actualizar(nota) > 0

        // Reprogramar alarma
        if (result) {
            alarmHelper?.programarAlarma(nota)
        }

        return result
    }

    suspend fun obtenerNotaPorId(id: Int): Nota? {
        return notaDao.obtenerNotaPorId(id)
    }

    suspend fun obtenerNotasPorFechaSync(fecha: String): List<Nota> {
        return notaDao.obtenerNotasPorFechaDirecto(fecha)
    }

    suspend fun eliminarNotaPorId(id: Int): Boolean {
        // Cancelar alarma
        alarmHelper?.cancelarAlarma(id)
        return notaDao.eliminarPorId(id) > 0
    }

    suspend fun obtenerNotasDeHoySync(): List<Nota> {
        return try {
            val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            withContext(Dispatchers.IO) {
                notaDao.obtenerNotasPorFechaSync(fechaHoy)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
