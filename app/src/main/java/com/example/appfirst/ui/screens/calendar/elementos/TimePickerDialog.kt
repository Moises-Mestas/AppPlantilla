package com.example.appfirst.ui.screens.calendar.elementos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TimePickerDialog(
    horaActual: String,
    onHoraSeleccionada: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val partes = horaActual.split(":")
    var hora by remember { mutableStateOf(partes[0].toInt()) }
    var minuto by remember { mutableStateOf(partes[1].toInt()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleccionar hora",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Selector de hora y minuto
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Selector de hora
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { hora = (hora + 1) % 24 },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowDropUp, contentDescription = "Incrementar hora")
                        }

                        Text(
                            text = hora.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        IconButton(
                            onClick = { hora = (hora - 1 + 24) % 24 },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Decrementar hora")
                        }
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Selector de minuto
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { minuto = (minuto + 5) % 60 },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowDropUp, contentDescription = "Incrementar minutos")
                        }

                        Text(
                            text = minuto.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        IconButton(
                            onClick = { minuto = (minuto - 5 + 60) % 60 },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Decrementar minutos")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val horaFormateada = "${hora.toString().padStart(2, '0')}:${minuto.toString().padStart(2, '0')}"
                            onHoraSeleccionada(horaFormateada)
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}