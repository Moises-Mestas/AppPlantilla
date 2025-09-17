package com.example.appfirst.ui.screens.calendar.elementos

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appfirst.data.repo.CalendarRepository
import com.example.appfirst.ui.screens.calendar.AccionDiariaViewModel
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModel

class NotaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotaViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AccionDiariaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccionDiariaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccionDiariaViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CalendarViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            val repository = CalendarRepository(
                com.example.appfirst.data.local.AppDatabase.get(application).ingresoDao()
            )
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}