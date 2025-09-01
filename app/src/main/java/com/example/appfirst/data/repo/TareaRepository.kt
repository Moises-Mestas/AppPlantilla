package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.TareaDao
import com.example.appfirst.data.local.entity.Tarea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TareaRepository(private val dao: TareaDao) {

    fun getTareasByUser(userId: Long): Flow<List<Tarea>> = dao.getTareasByUser(userId)

    fun getTareasByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Tarea>> =
        dao.getTareasByDateRange(userId, startDate, endDate)

    fun getTareasByAsignatura(userId: Long, asignatura: String): Flow<List<Tarea>> =
        dao.getTareasByAsignatura(userId, asignatura)

    fun getTareasByEstado(userId: Long, completada: Boolean): Flow<List<Tarea>> =
        dao.getTareasByEstado(userId, completada)

    suspend fun getTareaById(id: Long, userId: Long): Tarea? =
        dao.getTareaById(id, userId)

    suspend fun crearTarea(
        titulo: String,
        fechaEntrega: Long,
        fechaRecordatorio: Long,
        asignatura: String,
        nota: String? = null,
        archivos: List<String> = emptyList(),
        userId: Long
    ): Long {
        if (titulo.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }

        if (asignatura.isBlank()) {
            throw IllegalArgumentException("La asignatura es obligatoria")
        }

        if (fechaEntrega <= System.currentTimeMillis()) {
            throw IllegalArgumentException("La fecha de entrega debe ser futura")
        }

        val tarea = Tarea(
            titulo = titulo.trim(),
            fechaEntrega = fechaEntrega,
            fechaRecordatorio = fechaRecordatorio,
            asignatura = asignatura.trim(),
            nota = nota?.trim(),
            archivos = archivos,
            completada = false,
            userId = userId
        )

        return dao.insert(tarea)
    }

    suspend fun actualizarTarea(
        id: Long,
        titulo: String,
        fechaEntrega: Long,
        fechaRecordatorio: Long,
        asignatura: String,
        nota: String? = null,
        archivos: List<String> = emptyList(),
        userId: Long
    ) {
        val tareaExistente = dao.getTareaById(id, userId)
            ?: throw Exception("Tarea no encontrada")

        if (titulo.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }

        if (asignatura.isBlank()) {
            throw IllegalArgumentException("La asignatura es obligatoria")
        }

        val tareaActualizada = tareaExistente.copy(
            titulo = titulo.trim(),
            fechaEntrega = fechaEntrega,
            fechaRecordatorio = fechaRecordatorio,
            asignatura = asignatura.trim(),
            nota = nota?.trim(),
            archivos = archivos
        )

        dao.update(tareaActualizada)
    }

    suspend fun marcarCompletada(id: Long, userId: Long) {
        val tarea = dao.getTareaById(id, userId)
            ?: throw Exception("Tarea no encontrada")

        dao.update(tarea.copy(completada = true))
    }

    suspend fun marcarPendiente(id: Long, userId: Long) {
        val tarea = dao.getTareaById(id, userId)
            ?: throw Exception("Tarea no encontrada")

        dao.update(tarea.copy(completada = false))
    }

    suspend fun toggleCompletada(id: Long, userId: Long) {
        val tarea = dao.getTareaById(id, userId)
            ?: throw Exception("Tarea no encontrada")

        dao.update(tarea.copy(completada = !tarea.completada))
    }

    suspend fun eliminarTarea(id: Long, userId: Long) {
        val tarea = dao.getTareaById(id, userId)
            ?: throw Exception("Tarea no encontrada")

        dao.delete(tarea)
    }

    suspend fun eliminarTodasLasTareas(userId: Long) {
        dao.deleteAllByUser(userId)
    }

    fun getTareasProximas(userId: Long): Flow<List<Tarea>> {
        val ahora = System.currentTimeMillis()
        val en7Dias = ahora + (7 * 24 * 60 * 60 * 1000)
        return dao.getTareasByDateRange(userId, ahora, en7Dias)
    }

    suspend fun getTareasVencidas(userId: Long): List<Tarea> {
        val ahora = System.currentTimeMillis()
        val tareas = dao.getTareasByUser(userId).first()
        return tareas.filter { it.fechaEntrega < ahora && !it.completada }
    }

    fun getTareasDeHoy(userId: Long): Flow<List<Tarea>> {
        val hoy = System.currentTimeMillis()
        val manana = hoy + (24 * 60 * 60 * 1000)
        return dao.getTareasByDateRange(userId, hoy, manana)
    }
}