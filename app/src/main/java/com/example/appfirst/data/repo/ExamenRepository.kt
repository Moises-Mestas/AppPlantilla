package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.ExamenDao
import com.example.appfirst.data.local.entity.Examen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ExamenRepository(private val dao: ExamenDao) {

    fun getExamenesByUser(userId: Long): Flow<List<Examen>> =
        dao.getExamenesByUser(userId)

    fun getExamenesByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Examen>> =
        dao.getExamenesByDateRange(userId, startDate, endDate)

    fun getExamenesByCategoria(userId: Long, categoria: String): Flow<List<Examen>> =
        dao.getExamenesByCategoria(userId, categoria)

    fun getExamenesByAsignatura(userId: Long, asignatura: String): Flow<List<Examen>> =
        dao.getExamenesByAsignatura(userId, asignatura)

    suspend fun getExamenById(id: Long, userId: Long): Examen? =
        dao.getExamenById(id, userId)

    suspend fun crearExamen(
        titulo: String,
        fechaExamen: Long,
        fechaRecordatorio: Long,
        asignatura: String,
        categoria: String,
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

        if (categoria.isBlank()) {
            throw IllegalArgumentException("La categoría es obligatoria")
        }

        if (fechaExamen <= System.currentTimeMillis()) {
            throw IllegalArgumentException("La fecha del examen debe ser futura")
        }

        if (fechaRecordatorio >= fechaExamen) {
            throw IllegalArgumentException("La fecha de recordatorio debe ser anterior al examen")
        }

        val categoriasPermitidas = listOf("oral", "escrito", "práctico")
        if (!categoriasPermitidas.contains(categoria.lowercase())) {
            throw IllegalArgumentException("Categoría no válida. Use: oral, escrito, práctico")
        }

        val examen = Examen(
            titulo = titulo.trim(),
            fechaExamen = fechaExamen,
            fechaRecordatorio = fechaRecordatorio,
            asignatura = asignatura.trim(),
            categoria = categoria.lowercase(),
            nota = nota?.trim(),
            archivos = archivos,
            userId = userId
        )

        return dao.insert(examen)
    }

    suspend fun actualizarExamen(
        id: Long,
        titulo: String,
        fechaExamen: Long,
        fechaRecordatorio: Long,
        asignatura: String,
        categoria: String,
        nota: String? = null,
        archivos: List<String> = emptyList(),
        userId: Long
    ) {
        val examenExistente = dao.getExamenById(id, userId)
            ?: throw Exception("Examen no encontrado")

        if (titulo.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }

        if (asignatura.isBlank()) {
            throw IllegalArgumentException("La asignatura es obligatoria")
        }

        if (categoria.isBlank()) {
            throw IllegalArgumentException("La categoría es obligatoria")
        }

        if (fechaRecordatorio >= fechaExamen) {
            throw IllegalArgumentException("La fecha de recordatorio debe ser anterior al examen")
        }

        val categoriasPermitidas = listOf("oral", "escrito", "práctico")
        if (!categoriasPermitidas.contains(categoria.lowercase())) {
            throw IllegalArgumentException("Categoría no válida. Use: oral, escrito, práctico")
        }

        val examenActualizado = examenExistente.copy(
            titulo = titulo.trim(),
            fechaExamen = fechaExamen,
            fechaRecordatorio = fechaRecordatorio,
            asignatura = asignatura.trim(),
            categoria = categoria.lowercase(),
            nota = nota?.trim(),
            archivos = archivos,
            updatedAt = System.currentTimeMillis()
        )

        dao.update(examenActualizado)
    }

    suspend fun eliminarExamen(id: Long, userId: Long) {
        val examen = dao.getExamenById(id, userId)
            ?: throw Exception("Examen no encontrado")

        dao.delete(examen)
    }

    suspend fun eliminarTodosLosExamenes(userId: Long) {
        dao.deleteAllByUser(userId)
    }

    fun getExamenesProximos(userId: Long): Flow<List<Examen>> {
        val ahora = System.currentTimeMillis()
        val en7Dias = ahora + (7 * 24 * 60 * 60 * 1000) // 7 días en milisegundos
        return dao.getExamenesByDateRange(userId, ahora, en7Dias)
    }

    fun getExamenesDeHoy(userId: Long): Flow<List<Examen>> {
        val hoy = System.currentTimeMillis()
        val manana = hoy + (24 * 60 * 60 * 1000)
        return dao.getExamenesByDateRange(userId, hoy, manana)
    }

    suspend fun getExamenesPorTipo(userId: Long, tipo: String): List<Examen> {
        val examenes = dao.getExamenesByUser(userId).first()
        return examenes.filter { it.categoria.equals(tipo, ignoreCase = true) }
    }

    suspend fun getExamenesVencidos(userId: Long): List<Examen> {
        val ahora = System.currentTimeMillis()
        val examenes = dao.getExamenesByUser(userId).first()
        return examenes.filter { it.fechaExamen < ahora }
    }

    suspend fun buscarExamenes(userId: Long, query: String): List<Examen> {
        val examenes = dao.getExamenesByUser(userId).first()
        return examenes.filter {
            it.titulo.contains(query, ignoreCase = true) ||
                    it.asignatura.contains(query, ignoreCase = true) ||
                    it.nota?.contains(query, ignoreCase = true) ?: false
        }
    }

    suspend fun getEstadisticasExamenes(userId: Long): Map<String, Int> {
        val examenes = dao.getExamenesByUser(userId).first()
        return mapOf(
            "total" to examenes.size,
            "orales" to examenes.count { it.categoria == "oral" },
            "escritos" to examenes.count { it.categoria == "escrito" },
            "practicos" to examenes.count { it.categoria == "práctico" },
            "proximos" to examenes.count { it.fechaExamen > System.currentTimeMillis() },
            "vencidos" to examenes.count { it.fechaExamen < System.currentTimeMillis() }
        )
    }
}