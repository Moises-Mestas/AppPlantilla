package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.IngresoDao
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.data.local.entity.MedioPago

import com.example.appfirst.data.local.entity.TipoNota
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first



class IngresoRepository(private val dao: IngresoDao) {

    fun getIngresosByUser(userId: Long): Flow<List<Ingreso>> =
        dao.getIngresosByUser(userId)
    fun getIngresosByNotas(userId: Long, notas: TipoNota): Flow<List<Ingreso>> =
        dao.getIngresosByNotas(userId, notas)

    fun getIngresosByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Ingreso>> =
        dao.getIngresosByDateRange(userId, startDate, endDate)

    // 👇 AHORA acepta MedioPago (no String)
    fun getIngresosByDeposito(userId: Long, depositadoEn: MedioPago): Flow<List<Ingreso>> =
        dao.getIngresosByDeposito(userId, depositadoEn)

    fun getIngresosByMonto(userId: Long, monto: Double): Flow<List<Ingreso>> =
        dao.getIngresosByMonto(userId, monto)

    suspend fun getIngresoById(id: Int, userId: Long): Ingreso? =
        dao.getIngresoById(id, userId)
    suspend fun getAllIngresosSum(userId: Long): Double {
        return dao.getAllIngresosSum(userId) // Pasa el userId al DAO
    }


    suspend fun crearIngreso(
        monto: Double,
        descripcion: String,
        fecha: Long,
        depositadoEn: MedioPago,   // 👈 enum
        notas: TipoNota,
        userId: Long
    ): Long {
        if (descripcion.isBlank()) {
            throw IllegalArgumentException("La descripción es obligatoria")
        }
        // ❌ ya NO uses isBlank() sobre enum

        val ingreso = Ingreso(
            monto = monto,
            descripcion = descripcion.trim(),
            fecha = fecha,
            depositadoEn = depositadoEn,  // ✅ enum directo
            notas = notas,
            userId = userId
        )
        return dao.insert(ingreso)
    }

    suspend fun actualizarIngreso(
        id: Int,
        monto: Double,
        descripcion: String,
        fecha: Long,
        depositadoEn: MedioPago,   // 👈 enum
        notas: TipoNota,
        userId: Long
    ) {
        val ingresoExistente = dao.getIngresoById(id, userId)
            ?: throw Exception("Ingreso no encontrado")

        if (descripcion.isBlank()) {
            throw IllegalArgumentException("La descripción es obligatoria")
        }
        // ❌ nada de isBlank() aquí

        val ingresoActualizado = ingresoExistente.copy(
            monto = monto,
            descripcion = descripcion.trim(),
            fecha = fecha,
            depositadoEn = depositadoEn,  // ✅ enum
            notas = notas
        )
        dao.update(ingresoActualizado)
    }

    suspend fun eliminarIngreso(id: Int, userId: Long) {
        val ingreso = dao.getIngresoById(id, userId)
            ?: throw Exception("Ingreso no encontrado")
        dao.delete(ingreso)
    }

    suspend fun eliminarTodosLosIngresos(userId: Long) {
        dao.deleteAllByUser(userId)
    }

    fun getIngresosProximos(userId: Long): Flow<List<Ingreso>> {
        val ahora = System.currentTimeMillis()
        val en7Dias = ahora + (7 * 24 * 60 * 60 * 1000)
        return dao.getIngresosByDateRange(userId, ahora, en7Dias)
    }

    suspend fun getIngresosVencidos(userId: Long): List<Ingreso> {
        val ahora = System.currentTimeMillis()
        val ingresos = dao.getIngresosByUser(userId).first()
        return ingresos.filter { it.fecha < ahora }
    }

    fun getIngresosDeHoy(userId: Long): Flow<List<Ingreso>> {
        val hoy = System.currentTimeMillis()
        val manana = hoy + (24 * 60 * 60 * 1000)
        return dao.getIngresosByDateRange(userId, hoy, manana)
    }

    fun getGastosByDeposito(userId: Long, depositadoEn: MedioPago): Flow<List<Ingreso>> =
        dao.getGastosByDeposito(userId, depositadoEn)

}
