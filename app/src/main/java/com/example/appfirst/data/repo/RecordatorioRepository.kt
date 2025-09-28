package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.RecordatorioDao
import com.example.appfirst.data.local.entity.Recordatorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RecordatorioRepository(private val dao: RecordatorioDao) {

    fun getRecordatoriosByUser(userId: Long): Flow<List<Recordatorio>> =
        dao.getRecordatoriosByUser(userId)

    fun getRecordatoriosByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Recordatorio>> =
        dao.getRecordatoriosByDateRange(userId, startDate, endDate)

    suspend fun getRecordatorioById(id: Long, userId: Long): Recordatorio? =
        dao.getRecordatorioById(id, userId)

    suspend fun crearRecordatorio(
        titulo: String,
        fechaRecordatorio: Long,
        color: String,
        nota: String? = null,
        archivos: List<String> = emptyList(),
        userId: Long
    ): Long {
        if (titulo.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }

        if (color.isBlank()) {
            throw IllegalArgumentException("El color es obligatorio")
        }

        if (fechaRecordatorio <= System.currentTimeMillis()) {
            throw IllegalArgumentException("La fecha del recordatorio debe ser futura")
        }

        val recordatorio = Recordatorio(
            titulo = titulo.trim(),
            fechaRecordatorio = fechaRecordatorio,
            color = color.trim(),
            nota = nota?.trim(),
            archivos = archivos,
            userId = userId
        )

        return dao.insert(recordatorio)
    }

    suspend fun actualizarRecordatorio(
        id: Long,
        titulo: String,
        fechaRecordatorio: Long,
        color: String,
        nota: String? = null,
        archivos: List<String> = emptyList(),
        userId: Long
    ) {
        val recordatorioExistente = dao.getRecordatorioById(id, userId)
            ?: throw Exception("Recordatorio no encontrado")

        if (titulo.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }

        if (color.isBlank()) {
            throw IllegalArgumentException("El color es obligatorio")
        }

        val recordatorioActualizado = recordatorioExistente.copy(
            titulo = titulo.trim(),
            fechaRecordatorio = fechaRecordatorio,
            color = color.trim(),
            nota = nota?.trim(),
            archivos = archivos,
            updatedAt = System.currentTimeMillis()
        )

        dao.update(recordatorioActualizado)
    }

    suspend fun eliminarRecordatorio(id: Long, userId: Long) {
        val recordatorio = dao.getRecordatorioById(id, userId)
            ?: throw Exception("Recordatorio no encontrado")

        dao.delete(recordatorio)
    }

    suspend fun eliminarTodosLosRecordatorios(userId: Long) {
        dao.deleteAllByUser(userId)
    }

    fun getRecordatoriosProximos(userId: Long): Flow<List<Recordatorio>> {
        val ahora = System.currentTimeMillis()
        val en7Dias = ahora + (7 * 24 * 60 * 60 * 1000) // 7 días en milisegundos
        return dao.getRecordatoriosByDateRange(userId, ahora, en7Dias)
    }

    fun getRecordatoriosDeHoy(userId: Long): Flow<List<Recordatorio>> {
        val hoy = System.currentTimeMillis()
        val manana = hoy + (24 * 60 * 60 * 1000)
        return dao.getRecordatoriosByDateRange(userId, hoy, manana)
    }

    suspend fun getRecordatoriosByColor(userId: Long, color: String): List<Recordatorio> {
        val recordatorios = dao.getRecordatoriosByUser(userId).first()
        return recordatorios.filter { it.color.equals(color, ignoreCase = true) }
    }

    suspend fun buscarRecordatorios(userId: Long, query: String): List<Recordatorio> {
        val recordatorios = dao.getRecordatoriosByUser(userId).first()
        return recordatorios.filter {
            it.titulo.contains(query, ignoreCase = true) ||
                    it.nota?.contains(query, ignoreCase = true) ?: false
        }
    }
}