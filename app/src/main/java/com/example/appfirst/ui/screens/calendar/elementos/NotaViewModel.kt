package com.example.appfirst.ui.screens.calendar.elementos

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.data.repo.NotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotaViewModel(application: Application) : AndroidViewModel(application) {
    private val notaRepository: NotaRepository
    private val _notasState = MutableStateFlow<List<Nota>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val notasState: StateFlow<List<Nota>> = _notasState.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val database = AppDatabase.get(application)
        notaRepository = NotaRepository(database.notaDao())
        notaRepository.setAlarmHelper(application.applicationContext)
    }

    fun cargarNotasPorFecha(fecha: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val notas = withContext(Dispatchers.IO) {
                    notaRepository.obtenerNotasPorFechaSync(fecha)
                }
                _notasState.value = notas
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error cargando notas: ${e.message}")
                _errorMessage.value = "Error al cargar notas: ${e.message}"
                _notasState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun obtenerNotaPorId(id: Int): Nota? {
        return withContext(Dispatchers.IO) {
            try {
                notaRepository.obtenerNotaPorId(id)
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error obteniendo nota: ${e.message}")
                null
            }
        }
    }

    fun insertarNota(nota: Nota, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    notaRepository.crearNota(nota)
                }
                cargarNotasPorFecha(nota.fecha)
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error insertando nota: ${e.message}")
                val errorMsg = "Error al crear evento: ${e.message}"
                _errorMessage.value = errorMsg
                onError?.invoke(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarNota(nota: Nota, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    notaRepository.actualizarNota(nota)
                }
                cargarNotasPorFecha(nota.fecha)
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error actualizando nota: ${e.message}")
                val errorMsg = "Error al actualizar evento: ${e.message}"
                _errorMessage.value = errorMsg
                onError?.invoke(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarNota(id: Int, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nota = withContext(Dispatchers.IO) {
                    notaRepository.obtenerNotaPorId(id)
                }

                nota?.let {
                    withContext(Dispatchers.IO) {
                        notaRepository.eliminarNotaPorId(id)
                    }
                    cargarNotasPorFecha(it.fecha)
                    onSuccess?.invoke()
                } ?: run {
                    val errorMsg = "No se encontró el evento a eliminar"
                    _errorMessage.value = errorMsg
                    onError?.invoke(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error eliminando nota: ${e.message}")
                val errorMsg = "Error al eliminar evento: ${e.message}"
                _errorMessage.value = errorMsg
                onError?.invoke(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun obtenerNotasDeHoy() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val notasHoy = withContext(Dispatchers.IO) {
                    notaRepository.obtenerNotasDeHoySync()
                }
                _notasState.value = notasHoy
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error cargando notas de hoy: ${e.message}")
                _errorMessage.value = "Error al cargar eventos de hoy"
                _notasState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}