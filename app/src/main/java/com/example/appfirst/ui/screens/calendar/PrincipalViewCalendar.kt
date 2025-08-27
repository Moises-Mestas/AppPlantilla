package com.example.appfirst.ui.screens.calendar

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.material3.*

@Composable
fun CalendarioScreen(
    onNavigateToInicio: () -> Unit,
    onNavigateToDetalles: (String) -> Unit,
    onNavigateToNota: () -> Unit
) {
    var fecha by rememberSaveable { mutableStateOf("") }

    val calendario = Calendar.getInstance()
    val año = calendario.get(Calendar.YEAR)
    val mes = calendario.get(Calendar.MONTH)
    val dia = calendario.get(Calendar.DAY_OF_MONTH)

    val context = LocalContext.current

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            fecha = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        año, mes, dia
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Boton superior izquierdo
        Button(
            onClick = onNavigateToInicio,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("← Inicio")
        }

        // Seleccioanr fecha
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                readOnly = true,
                label = { Text(text = "Seleccione el día") },
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Abrir calendario",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { datePickerDialog.show() }
                    .padding(start = 8.dp)
            )
        }

        // Botón inferior central
        Button(
            onClick = { onNavigateToDetalles("17 Diciembre") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Text("Ver Detalles")
        }

        // Botón para añadir nota
        Button(
            onClick = { onNavigateToNota() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Añadir nota")
        }
    }
}
