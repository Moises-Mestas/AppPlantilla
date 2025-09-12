package com.example.appfirst.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.repo.AccionDiariaRepository
import com.example.appfirst.data.repo.NotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HorarioDiarioViewModel(
    application: Application,
    private val accionDiariaRepository: AccionDiariaRepository,
    private val notaRepository: NotaRepository
) : AndroidViewModel(application) {

    private val _estado = MutableStateFlow(HorarioDiarioEstado())
    val estado: StateFlow<HorarioDiarioEstado> = _estado.asStateFlow()

    init {
        cargarDatosDeHoy()
    }

    fun cargarDatosDeHoy() {
        viewModelScope.launch {
            try {
                val fechaHoy = obtenerFechaHoy()
                val acciones = accionDiariaRepository.obtenerAccionesDeHoy()
                val notas = notaRepository.obtenerNotasPorFechaSync(fechaHoy)

                _estado.value = HorarioDiarioEstado(
                    acciones = acciones,
                    notas = notas,
                    isLoading = false
                )
            } catch (e: Exception) {
                _estado.value = HorarioDiarioEstado(
                    error = "Error al cargar datos: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun obtenerFechaHoy(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Función para obtener el nombre del día en español
    fun obtenerDiaDeLaSemanaHoy(): String {
        val fecha = Date()
        val formato = SimpleDateFormat("EEEE", Locale.getDefault())
        val diaIngles = formato.format(fecha)

        // Convertir a español
        return when (diaIngles.toLowerCase()) {
            "monday" -> "Lunes"
            "tuesday" -> "Martes"
            "wednesday" -> "Miércoles"
            "thursday" -> "Jueves"
            "friday" -> "Viernes"
            "saturday" -> "Sábado"
            "sunday" -> "Domingo"
            else -> diaIngles
        }
    }
}