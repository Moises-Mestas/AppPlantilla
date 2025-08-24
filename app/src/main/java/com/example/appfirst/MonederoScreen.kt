package com.example.appfirst

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MonederoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen del Monedero
        Image(
            painter = painterResource(id = R.drawable.dinero),  // Reemplaza con tu imagen en drawable
            contentDescription = "Imagen del Monedero",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 32.dp)
                .clickable {
                    // Navegar a la pantalla Cuentas
                    navController.navigate("cuentas")
                }
        )

        // Texto relacionado al Monedero
        Text(
            text = "Bienvenido a tu Monedero",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
