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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FormularioNotaScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit
){
    var descripcion by remember { mutableStateOf("") }
    var colorSeleccionado by remember { mutableStateOf("Azul") }
    var repeticion by remember { mutableStateOf("Ninguno") }

    Column (modifier = Modifier.padding(16.dp)) {
        Text("Añadir Nota", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Color:")
        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Gris", "Verde", "Amarillo", "Rojo", "Azul").forEach { color ->
                Button (
                    onClick = { colorSeleccionado = color },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colorSeleccionado == color) Color(0xFF2196F3) else Color.LightGray
                    )
                ) {
                    Text(color)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recordatorio:")
        OutlinedTextField(
            value = "15:45",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Repetición:")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Ninguno", "Diario", "Semanal", "Mensual", "Anual").forEach { rep ->
                Button(
                    onClick = { repeticion = rep },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (repeticion == rep) Color(0xFF2196F3) else Color.LightGray
                    )
                ) {
                    Text(rep)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancelar", color = Color.White)
            }

            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Guardar", color = Color.White)
            }
        }
    }
}
