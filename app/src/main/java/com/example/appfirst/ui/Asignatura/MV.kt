package com.example.appfirst.ui.asignatura

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun rememberAsignaturaVM(): AsignaturaViewModel {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as Application
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AsignaturaViewModel(app) as T
        }
    })
}