package com.example.appfirst.ui.screens.calendar.elementos

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appfirst.data.local.AppDatabase
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

class CalendarViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            val database = AppDatabase.get(application)
            val repository = CalendarRepository(database.ingresoDao())
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class LocalNavDestination(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    HOME(Icons.Default.Home, "Inicio", "Icono de inicio"),
    CALENDAR(Icons.Default.DateRange, "Calendario", "Icono de calendario"),
    SCHEDULE(Icons.Default.List, "Horario", "Icono de horario"),
    SAVINGS(Icons.Default.Face, "Ahorros", "Icono de ahorros"),
    TASKS(Icons.Default.AccountBox, "Agenda", "Icono de agenda")
}