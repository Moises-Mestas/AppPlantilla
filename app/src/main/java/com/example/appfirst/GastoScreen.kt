package com.example.appfirst

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GastoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título para "Gasto"
        Text(
            text = "Gasto",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // Cuadro de Gasto con contenido
        CuentaItem(
            titulo = "Gastos:",
            detalles = listOf(
                "Compra de Ropa: $/ 150.00",
                "Pago de Alquiler: $/ 500.00"
            )
        )
    }
}
