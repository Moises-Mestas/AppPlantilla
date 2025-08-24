package com.example.appfirst

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IngresoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título para "Ingreso"
        Text(
            text = "Ingreso",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // Cuadro de Ingreso con contenido
        CuentaItem(
            titulo = "Ingreso:",
            detalles = listOf(
                "Salario: $/ 1000.00",
                "Bono: $/ 200.00"
            )
        )
    }
}
