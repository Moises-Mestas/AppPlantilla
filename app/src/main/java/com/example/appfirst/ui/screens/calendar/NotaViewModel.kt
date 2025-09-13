package com.example.appfirst.ui.screens.calendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.entity.Nota
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.repo.NotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaViewModel(application: Application) : AndroidViewModel(application) {
    private val notaRepository: NotaRepository
    private val _notasState = MutableStateFlow<List<Nota>>(emptyList())
    val notasState: StateFlow<List<Nota>> = _notasState.asStateFlow()

    init {
        val database = AppDatabase.get(application)
        notaRepository = NotaRepository(database.notaDao())
    }

    fun cargarNotasPorFecha(fecha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notas = notaRepository.obtenerNotasPorFechaSync(fecha)
                withContext(Dispatchers.Main) {
                    _notasState.value = notas
                }
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error cargando notas: ${e.message}")
                withContext(Dispatchers.Main) {
                    _notasState.value = emptyList()
                }
            }
        }
    }

    suspend fun obtenerNotaPorId(id: Int): Nota? {
        return withContext(Dispatchers.IO) {
            notaRepository.obtenerNotaPorId(id)
        }
    }

    fun insertarNota(nota: Nota) {
        viewModelScope.launch(Dispatchers.IO) {
            notaRepository.crearNota(nota)
            // Recargar después de insertar
            cargarNotasPorFecha(nota.fecha)
        }
    }

    fun actualizarNota(nota: Nota) {
        viewModelScope.launch(Dispatchers.IO) {
            notaRepository.actualizarNota(nota)
            // Recargar después de actualizar
            cargarNotasPorFecha(nota.fecha)
        }
    }

    fun eliminarNota(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Primero obtener la nota para saber la fecha
            val nota = notaRepository.obtenerNotaPorId(id)
            nota?.let {
                notaRepository.eliminarNotaPorId(id)
                // Recargar después de eliminar
                cargarNotasPorFecha(it.fecha)
            }
        }
    }
}