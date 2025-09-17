package com.example.appfirst.ui.screens.calendar.elementos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.data.repo.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private val _movimientos = MutableStateFlow<List<Ingreso>>(emptyList())
    val movimientos: StateFlow<List<Ingreso>> = _movimientos.asStateFlow()

    // Agregar esto para agrupar por fecha
    val movimientosPorFecha: StateFlow<Map<String, List<Ingreso>>>
        get() = _movimientos.map { movimientos ->
            movimientos.groupBy { ingreso ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(ingreso.fecha))
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

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