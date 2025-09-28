package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.IngresoDao
import com.example.appfirst.data.local.entity.Ingreso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val ingresoDao: IngresoDao
) {
    // Para movimientos por fecha específica
    suspend fun getMovimientosByFecha(userId: Long, fecha: String): List<Ingreso> {
        return ingresoDao.getIngresosByUserAndDate(userId, fecha)
    }

    // Para todos los movimientos del usuario (flow para observación continua)
    fun getMovimientosByUser(userId: Long): Flow<List<Ingreso>> {
        return ingresoDao.getIngresosByUserId(userId)
    }

    // Para calcular el balance de un día específico
    suspend fun getBalanceDelDia(userId: Long, fecha: String): Double {
        val movimientos = getMovimientosByFecha(userId, fecha)
        return movimientos.sumOf { it.monto }
    }
}