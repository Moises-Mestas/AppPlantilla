package com.example.appfirst.ui.examen

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun rememberExamenVM(): ExamenViewModel {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as Application
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ExamenViewModel(app) as T
        }
    })
}