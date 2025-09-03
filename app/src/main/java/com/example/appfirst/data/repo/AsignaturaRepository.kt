package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.AsignaturaDao
import com.example.appfirst.data.local.entity.Asignatura
import kotlinx.coroutines.flow.Flow

class AsignaturaRepository(
    private val asignaturaDao: AsignaturaDao
) {

    // Insertar asignatura
    suspend fun insertAsignatura(asignatura: Asignatura): Long {
        return asignaturaDao.insert(asignatura)
    }

    // Actualizar asignatura
    suspend fun updateAsignatura(asignatura: Asignatura) {
        asignaturaDao.update(asignatura)
    }

    // Eliminar asignatura
    suspend fun deleteAsignatura(asignatura: Asignatura) {
        asignaturaDao.delete(asignatura)
    }

    // Obtener todas las asignaturas de un usuario
    fun getAsignaturasByUser(userId: Long): Flow<List<Asignatura>> {
        return asignaturaDao.getAsignaturasByUser(userId)
    }

    // Obtener asignatura por ID
    suspend fun getAsignaturaById(id: Long, userId: Long): Asignatura? {
        return asignaturaDao.getAsignaturaById(id, userId)
    }

    // Buscar asignaturas por nombre
    fun searchAsignaturas(userId: Long, query: String): Flow<List<Asignatura>> {
        return asignaturaDao.searchAsignaturas(userId, query)
    }

    // Eliminar todas las asignaturas de un usuario
    suspend fun deleteAllAsignaturasByUser(userId: Long) {
        asignaturaDao.deleteAllByUser(userId)
    }

    // Verificar si ya existe una asignatura con el mismo nombre
    suspend fun existsAsignatura(userId: Long, nombre: String): Boolean {
        return asignaturaDao.existsAsignatura(userId, nombre) > 0
    }

    // Obtener asignatura por nombre exacto
    suspend fun getAsignaturaByNombre(userId: Long, nombre: String): Asignatura? {
        return asignaturaDao.getAsignaturaByNombre(userId, nombre)
    }

    // Crear una nueva asignatura con validación
    suspend fun createAsignatura(
        nombre: String,
        profesor: String,
        aula: String,
        userId: Long
    ): Result<Long> {
        return try {
            // Verificar si ya existe
            if (existsAsignatura(userId, nombre)) {
                return Result.failure(Exception("Ya existe una asignatura con este nombre"))
            }

            val asignatura = Asignatura(
                nombre = nombre,
                profesor = profesor,
                aula = aula,
                userId = userId
            )

            val id = insertAsignatura(asignatura)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}