package com.example.appfirst.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.*

@Composable
fun MyDatePickerDialog(
    onDateSelected: (Calendar) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedHour by remember { mutableStateOf(selectedDate.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(selectedDate.get(Calendar.MINUTE)) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Seleccionar Fecha y Hora",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Selector de fecha (puedes usar una librería mejor)
                // Por ahora un input simple
                OutlinedTextField(
                    value = "${selectedDate.get(Calendar.DAY_OF_MONTH)}/" +
                            "${selectedDate.get(Calendar.MONTH) + 1}/" +
                            "${selectedDate.get(Calendar.YEAR)}",
                    onValueChange = { /* Implementar parser de fecha */ },
                    label = { Text("Fecha") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedHour.toString(),
                        onValueChange = { selectedHour = it.toIntOrNull() ?: 0 },
                        label = { Text("Hora") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = selectedMinute.toString(),
                        onValueChange = { selectedMinute = it.toIntOrNull() ?: 0 },
                        label = { Text("Minutos") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance().apply {
                                time = selectedDate.time
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                            }
                            onDateSelected(calendar)
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}