package com.example.appfirst.ui.screens.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VistaDetallesDiaScreen(
    fecha: String,
    onBackToCalendario: () -> Unit
) {
    var seccionActiva by remember { mutableStateOf("Notas") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Botón para volver a Calendario
        Button(
            onClick = onBackToCalendario,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Calendario")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "Detalles de $fecha",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { seccionActiva = "Notas" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seccionActiva == "Notas") Color(0xFF2196F3) else Color.LightGray
                )
            ) {
                Text("Notas", color = Color.White)
            }

            Button(
                onClick = { seccionActiva = "Movimientos" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seccionActiva == "Movimientos") Color(0xFF2196F3) else Color.LightGray
                )
            ) {
                Text("Movimientos", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contenido simulado
        when (seccionActiva) {
            "Notas" -> {
                Text("Notas registradas:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("- Reunión con cliente")
                    Text("- Recordatorio de pago")
                    Text("- Meta de ahorro semanal")
                }
            }
            "Movimientos" -> {
                Text("Movimientos del día:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("- + S/ 50.00 (Ingreso)")
                    Text("- – S/ 20.00 (Gasto)")
                    Text("- – S/ 5.00 (Transporte)")
                }
            }
        }
    }
}
