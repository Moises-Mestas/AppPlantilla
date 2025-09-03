package com.example.appfirst.ui.screens.calendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
    private val repository: NotaRepository
    private val _insertResult = MutableStateFlow<Long?>(null)
    private val _notasPorFecha = MutableStateFlow<List<Nota>>(emptyList())

    val insertResult: StateFlow<Long?> = _insertResult.asStateFlow()
    val notasPorFecha: StateFlow<List<Nota>> = _notasPorFecha.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val notaDao = AppDatabase.getDatabase(application).notaDao()
        repository = NotaRepository(notaDao)
    }

    fun insertarNota(nota: Nota) {
        Log.d("NotaViewModel", "Insertando nota: $nota")

        if (nota.titulo.isBlank() || nota.fecha.isBlank()) {
            Log.e("NotaViewModel", "Nota inválida: título o fecha vacíos")
            _errorMessage.value = "Título y fecha son obligatorios"
            return
        }

        viewModelScope.launch {
            try {
                val id = repository.insert(nota)
                _insertResult.value = id
                Log.d("NotaViewModel", "Nota insertada correctamente con ID: $id")
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error al insertar nota: ${e.message}")
                _errorMessage.value = "Error al guardar: ${e.message}"
            }
        }
    }

    fun obtenerNotasPorFecha(fecha: String): LiveData<List<Nota>> {
        Log.d("NotaViewModel", "Buscando notas para fecha: $fecha")
        return repository.getPorFecha(fecha)
    }
    suspend fun obtenerNotasDirectamente(fecha: String): List<Nota> {
        return withContext(Dispatchers.IO) {
            try {
                repository.getPorFechaDirecto(fecha)
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error obteniendo notas: ${e.message}")
                emptyList()
            }
        }
    }

    fun obtenerTodasLasNotas(): LiveData<List<Nota>> {
        return repository.getTodas()
    }

    fun obtenerNotasPorRangoFechas(startDate: String, endDate: String): LiveData<List<Nota>> {
        return repository.getPorRangoFechas(startDate, endDate)
    }

    fun eliminarNota(nota: Nota) {
        viewModelScope.launch {
            try {
                repository.delete(nota)
                Log.d("NotaViewModel", "Nota eliminada correctamente")
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error al eliminar nota: ${e.message}")
                _errorMessage.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun actualizarNota(nota: Nota) {
        viewModelScope.launch {
            try {
                repository.update(nota)
                Log.d("NotaViewModel", "Nota actualizada correctamente")
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error al actualizar nota: ${e.message}")
                _errorMessage.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    // Limpiar mensajes de error
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearInsertResult() {
        _insertResult.value = null
    }
}
