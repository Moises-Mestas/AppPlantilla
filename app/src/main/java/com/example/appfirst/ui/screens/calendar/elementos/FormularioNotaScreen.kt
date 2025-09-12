package com.example.appfirst.ui.screens.calendar.elementos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appfirst.data.local.entity.Nota

@Composable
fun FormularioNotaScreen(
    fecha: String,
    notaExistente: Nota? = null,
    onCancel: () -> Unit,
    onSave: (Nota) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(notaExistente?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(notaExistente?.descripcion ?: "") }
    var horaInicio by remember { mutableStateOf(notaExistente?.horaInicio ?: "08:00") }
    var horaFin by remember { mutableStateOf(notaExistente?.horaFin ?: "09:00") }
    var color by remember { mutableStateOf(notaExistente?.color ?: 0xFF2196F3.toInt()) }
    var tipo by remember { mutableStateOf(notaExistente?.tipo ?: "Evento") }
    var categoria by remember { mutableStateOf(notaExistente?.categoria ?: "Personal") }
    var prioridad by remember { mutableStateOf(notaExistente?.prioridad ?: 3) }

    val colores = listOf(
        0xFF2196F3.toInt(), 0xFF4CAF50.toInt(), 0xFFFFC107.toInt(),
        0xFFF44336.toInt(), 0xFF9C27B0.toInt(), 0xFFFF9800.toInt()
    )

    val tipos = listOf("Evento", "Tarea", "Recordatorio")
    val categoriasList = listOf("Trabajo", "Estudio", "Ejercicio", "Personal", "Salud", "Otros")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header con botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Cancelar")
            }

            Text(
                text = if (notaExistente == null) "Nuevo Evento" else "Editar Evento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (notaExistente != null && onDelete != null) {
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campos del formulario
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título del evento *") },
            modifier = Modifier.fillMaxWidth(),
//            colors = outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline
//            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline
//            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de horas
        Text(
            text = "Horario:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hora inicio:", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Hora fin:", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { horaFin = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de color
        Text(
            text = "Color:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(colores.size) { index ->
                val colorOption = colores[index]
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(colorOption), CircleShape)
                        .border(
                            width = if (color == colorOption) 3.dp else 1.dp,
                            color = if (color == colorOption) MaterialTheme.colorScheme.primary
                            else Color.Gray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { color = colorOption }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de tipo
        SelectorOpcionesMejorado(
            etiqueta = "Tipo:",
            opciones = tipos,
            valorActual = tipo,
            onValorCambiado = { tipo = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de categoría
        SelectorOpcionesMejorado(
            etiqueta = "Categoría:",
            opciones = categoriasList,
            valorActual = categoria,
            onValorCambiado = { categoria = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de prioridad
        Text(
            text = "Prioridad:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (1..5).forEach { nivel ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (prioridad == nivel) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                        .clickable { prioridad = nivel }
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nivel.toString(),
                        color = if (prioridad == nivel) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val nota = Nota(
                        id = notaExistente?.id ?: 0,
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        color = color.toInt(),
                        tipo = tipo,
                        horaRecordatorio = if (tipo == "Recordatorio") horaInicio else null,
                        repeticion = "Ninguno",
                        categoria = categoria,
                        prioridad = prioridad
                    )
                    onSave(nota)
                },
                enabled = titulo.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (notaExistente == null) "Crear" else "Actualizar")
            }
        }
    }
}

@Composable
fun SelectorOpcionesMejorado(
    etiqueta: String,
    opciones: List<String>,
    valorActual: String,
    onValorCambiado: (String) -> Unit
) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(opciones.size) { index ->
                val opcion = opciones[index]
                FilterChip(
                    selected = valorActual == opcion,
                    onClick = { onValorCambiado(opcion) },
                    label = { Text(opcion) },
                    leadingIcon = {
                        if (valorActual == opcion) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
