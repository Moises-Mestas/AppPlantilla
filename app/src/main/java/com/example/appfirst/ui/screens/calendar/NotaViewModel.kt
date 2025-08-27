package com.example.appfirst.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.data.repo.NotaRepository
import kotlinx.coroutines.launch

class NotaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).notaDao()
    private val repo = NotaRepository(dao)

    fun insertarNota(nota: Nota) {
        viewModelScope.launch {
            repo.insert(nota)
        }
    }

    fun obtenerNotasPorFecha(fecha: String): LiveData<List<Nota>> = liveData {
        emit(repo.getPorFecha(fecha))
    }

    fun eliminarNota(nota: Nota) {
        viewModelScope.launch {
            repo.delete(nota)
        }
    }

    fun actualizarNota(nota: Nota) {
        viewModelScope.launch {
            repo.update(nota)
        }
    }
}
