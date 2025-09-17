package com.example.appfirst.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.data.repo.AccionDiariaRepository
import com.example.appfirst.data.repo.NotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
class AccionDiariaViewModel(application: Application) : AndroidViewModel(application) {
    private val accionDiariaRepository: AccionDiariaRepository
    private val notaRepository: NotaRepository

    private val _horarioEstado = MutableStateFlow(HorarioDiarioEstado())
    val horarioEstado: StateFlow<HorarioDiarioEstado> = _horarioEstado.asStateFlow()

    private val _accionesFiltradas = MutableStateFlow<List<AccionDiaria>>(emptyList())
    val accionesFiltradas: StateFlow<List<AccionDiaria>> = _accionesFiltradas.asStateFlow()

    // Filtros
    private var _filtroTexto = ""
    private var _filtroDia = "Todos"
    private var _filtroCategoria = "Todas"

    init {
        val database = AppDatabase.get(application)
        accionDiariaRepository = AccionDiariaRepository(database.accionDiariaDao())
        notaRepository = NotaRepository(database.notaDao())

        cargarHorarioDeHoy()
        cargarAccionesFiltradas()
    }

    fun setFiltros(texto: String, dia: String, categoria: String) {
        _filtroTexto = texto
        _filtroDia = dia
        _filtroCategoria = categoria
        cargarAccionesFiltradas()
    }

    fun eliminarAccion(accion: AccionDiaria) {
        viewModelScope.launch {
            accionDiariaRepository.delete(accion)
            cargarHorarioDeHoy()
            cargarAccionesFiltradas()
        }
    }

    private fun cargarAccionesFiltradas() {
        viewModelScope.launch {
            try {
                val acciones = accionDiariaRepository.getAccionesFiltradasSync(
                    _filtroTexto, _filtroDia, _filtroCategoria
                )
                _accionesFiltradas.value = acciones
            } catch (e: Exception) {
                _accionesFiltradas.value = emptyList()
            }
        }
    }

    fun cargarHorarioDeHoy() {
        viewModelScope.launch {
            try {
                val accionesHoy = accionDiariaRepository.obtenerAccionesDeHoy()
                val notasHoy = notaRepository.obtenerNotasDeHoySync()

                _horarioEstado.value = HorarioDiarioEstado(
                    acciones = accionesHoy,
                    notas = notasHoy,
                    isLoading = false
                )
            } catch (e: Exception) {
                _horarioEstado.value = HorarioDiarioEstado(
                    error = "Error al cargar datos: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    suspend fun obtenerAccionPorId(id: Int): AccionDiaria? {
        return accionDiariaRepository.getAccionPorId(id)
    }

    fun insertarAccion(accion: AccionDiaria) {
        viewModelScope.launch {
            accionDiariaRepository.insert(accion)
            // Recargar después de insertar
            cargarHorarioDeHoy()
            cargarAccionesFiltradas()
        }
    }

    fun editarAccion(accion: AccionDiaria) {
        viewModelScope.launch {
            accionDiariaRepository.update(accion)
            cargarHorarioDeHoy()
            cargarAccionesFiltradas()
        }
    }

    fun eliminarAccionPorId(accionId: Int) {
        viewModelScope.launch {
            accionDiariaRepository.deleteAccionPorId(accionId)
            cargarHorarioDeHoy()
            cargarAccionesFiltradas()
        }
    }

    fun obtenerDiaDeLaSemanaHoy(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }
    }
}

data class HorarioDiarioEstado(
    val acciones: List<AccionDiaria> = emptyList(),
    val notas: List<Nota> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)