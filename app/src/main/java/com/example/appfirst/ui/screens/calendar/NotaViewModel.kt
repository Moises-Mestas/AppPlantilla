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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotaRepository
    private val _notas = MutableStateFlow<List<Nota>>(emptyList())
    val notas: StateFlow<List<Nota>> = _notas.asStateFlow()

    init {
        val notaDao = AppDatabase.get(application).notaDao()
        repository = NotaRepository(notaDao)
    }

    fun crearNota(nota: Nota) {
        viewModelScope.launch {
            try {
                repository.crearNota(nota)
                // Agregar la nueva nota a la lista local
                _notas.value = _notas.value + nota
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error al crear nota: ${e.message}")
            }
        }
    }

    fun actualizarNota(nota: Nota) {
        viewModelScope.launch {
            try {
                repository.actualizarNota(nota)
                // Actualizar la lista local reemplazando la nota
                _notas.value = _notas.value.map { if (it.id == nota.id) nota else it }
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error al actualizar nota: ${e.message}")
            }
        }
    }

    suspend fun obtenerNotaPorId(id: Int): Nota? {
        return try {
            repository.obtenerNotaPorId(id)
        } catch (e: Exception) {
            null
        }
    }

    fun eliminarNota(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarNotaPorId(id)
                // Actualizar la lista local eliminando la nota
                _notas.value = _notas.value.filter { it.id != id }
            } catch (e: Exception) {
                Log.e("NotaViewModel", "Error al eliminar nota: ${e.message}")
            }
        }
    }

    fun cargarNotasPorFecha(fecha: String) {
        viewModelScope.launch {
            try {
                val notasList = repository.obtenerNotasPorFechaSync(fecha)
                _notas.value = notasList
            } catch (e: Exception) {
                _notas.value = emptyList()
            }
        }
    }

//    fun obtenerNotasDeHoy(): Flow<List<Nota>> {
//        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        return repository.obtenerNotasPorFecha(fechaHoy)
//    }

    suspend fun obtenerNotasDeHoySync(): List<Nota> {
        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return repository.obtenerNotasPorFechaSync(fechaHoy)
    }


}