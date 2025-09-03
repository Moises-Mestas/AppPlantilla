package com.example.appfirst.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.data.repo.AccionDiariaRepository
import kotlinx.coroutines.launch

class AccionDiariaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AccionDiariaRepository

    private val _filtroTexto = MutableLiveData("")
    private val _filtroDia = MutableLiveData("Todos")
    private val _filtroCategoria = MutableLiveData("Todas")

    val accionesFiltradas: LiveData<List<AccionDiaria>>

    init {
        val accionDao = AppDatabase.getDatabase(application).accionDiariaDao()
        repository = AccionDiariaRepository(accionDao)

        accionesFiltradas = MediatorLiveData<List<AccionDiaria>>().apply {
            fun update() {
                val texto = _filtroTexto.value ?: ""
                val dia = _filtroDia.value ?: "Todos"
                val categoria = _filtroCategoria.value ?: "Todas"
                val source = repository.getAccionesFiltradas(texto, dia, categoria)
                removeSource(source)
                addSource(source) { value = it }
            }

            addSource(_filtroTexto) { update() }
            addSource(_filtroDia) { update() }
            addSource(_filtroCategoria) { update() }
        }
    }

    fun setFiltros(texto: String, dia: String, categoria: String) {
        _filtroTexto.value = texto
        _filtroDia.value = dia
        _filtroCategoria.value = categoria
    }

    suspend fun obtenerAccionPorId(id: Int): AccionDiaria? {
        return repository.getAccionPorId(id)
    }

    fun insertarAccion(accion: AccionDiaria) {
        viewModelScope.launch {
            repository.insert(accion)
        }
    }

    fun actualizarAccion(accion: AccionDiaria) {
        viewModelScope.launch {
            repository.update(accion)
        }
    }

    fun eliminarAccion(accion: AccionDiaria) {
        viewModelScope.launch {
            repository.delete(accion)
        }
    }
}