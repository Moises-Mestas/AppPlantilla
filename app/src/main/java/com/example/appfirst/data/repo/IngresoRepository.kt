package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.IngresoDao
import com.example.appfirst.data.local.entity.Ingreso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class IngresoRepository(private val dao: IngresoDao) {

    fun getIngresosByUser(userId: Long): Flow<List<Ingreso>> = dao.getIngresosByUser(userId)

    fun getIngresosByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Ingreso>> =
        dao.getIngresosByDateRange(userId, startDate, endDate)

    fun getIngresosByDeposito(userId: Long, depositadoEn: String): Flow<List<Ingreso>> =
        dao.getIngresosByDeposito(userId, depositadoEn)

    fun getIngresosByMonto(userId: Long, monto: Double): Flow<List<Ingreso>> =
        dao.getIngresosByMonto(userId, monto)

    suspend fun getIngresoById(id: Int, userId: Long): Ingreso? =
        dao.getIngresoById(id, userId)

    suspend fun crearIngreso(
        monto: Double,
        descripcion: String,
        fecha: Long,
        depositadoEn: String,
        notas: String,
        userId: Long
    ): Long {
        if (descripcion.isBlank()) {
            throw IllegalArgumentException("La descripción es obligatoria")
        }

        if (depositadoEn.isBlank()) {
            throw IllegalArgumentException("El lugar de depósito es obligatorio")
        }

        val ingreso = Ingreso(
            monto = monto,
            descripcion = descripcion.trim(),
            fecha = fecha,
            depositadoEn = depositadoEn.trim(),
            notas = notas.trim(),
            userId = userId
        )

        return dao.insert(ingreso)
    }

    suspend fun actualizarIngreso(
        id: Int,
        monto: Double,
        descripcion: String,
        fecha: Long,
        depositadoEn: String,
        notas: String,
        userId: Long
    ) {
        val ingresoExistente = dao.getIngresoById(id, userId)
            ?: throw Exception("Ingreso no encontrado")

        if (descripcion.isBlank()) {
            throw IllegalArgumentException("La descripción es obligatoria")
        }

        if (depositadoEn.isBlank()) {
            throw IllegalArgumentException("El lugar de depósito es obligatorio")
        }

        val ingresoActualizado = ingresoExistente.copy(
            monto = monto,
            descripcion = descripcion.trim(),
            fecha = fecha,
            depositadoEn = depositadoEn.trim(),
            notas = notas.trim()
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
}
