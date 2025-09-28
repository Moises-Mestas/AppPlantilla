package com.example.appfirst.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.appfirst.R
import com.example.appfirst.data.datastore.UserPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Detectar el clic en cualquier parte de la pantalla para navegar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable() {
                scope.launch(Dispatchers.IO) {
                    UserPrefs.setOnboardDone(ctx, true)
                    withContext(Dispatchers.Main) { onFinish() }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Imagen centrada en el medio de la pantalla
        Image(
            painter = painterResource(id = R.drawable.logo1), // Asegúrate de que el nombre sea correcto
            contentDescription = "Onboarding Image",
            modifier = Modifier.size(500.dp) // Ajusta el tamaño de la imagen según sea necesario
        )
    }
}

