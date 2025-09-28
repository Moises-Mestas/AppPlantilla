package com.example.appfirst.ui.screens.calendar.elementos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.data.repo.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//@HiltViewModel
class CalendarViewModel(
    private val calendarRepository: CalendarRepository // ← Inyectado manualmente
) : ViewModel() {

    private val _movimientos = MutableStateFlow<List<Ingreso>>(emptyList())
    val movimientos: StateFlow<List<Ingreso>> = _movimientos.asStateFlow()

    fun cargarMovimientos(userId: Long) {
        viewModelScope.launch {
            calendarRepository.getMovimientosByUser(userId).collect { movimientos ->
                _movimientos.value = movimientos
            }
        }
    }

    suspend fun getMovimientosDelDia(userId: Long, fecha: String): List<Ingreso> {
        return calendarRepository.getMovimientosByFecha(userId, fecha)
    }

    suspend fun getBalanceDelDia(userId: Long, fecha: String): Double {
        return calendarRepository.getBalanceDelDia(userId, fecha)
    }
}