package com.example.appfirst.ui.screens.calendar

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfirst.data.local.entity.Nota

@Composable
fun FormularioNotaScreen(
    fecha: String,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("09:00") }
    var horaFin by remember { mutableStateOf("10:00") }
    var colorSeleccionado by remember { mutableStateOf(0xFF2196F3) }
    var tipo by remember { mutableStateOf("Evento") }
    var repeticion by remember { mutableStateOf("Ninguno") }
    var categoria by remember { mutableStateOf("Personal") }
    var prioridad by remember { mutableStateOf(3) }

    val viewModel: NotaViewModel = viewModel(
        factory = NotaViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )

    // Observar los resultados de la inserción
    val insertResult by viewModel.insertResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Efecto para manejar el resultado exitoso
    LaunchedEffect(key1 = insertResult) {
        insertResult?.let { id ->
            Log.d("FormularioNota", "Nota guardada con ID: $id")
            onSave()
            viewModel.clearInsertResult()
        }
    }

    // Mostrar errores
    val context = LocalContext.current

    LaunchedEffect(key1 = errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
            viewModel.clearErrorMessage()
        }
    }

    val colores = listOf(0xFF2196F3, 0xFF4CAF50, 0xFFFFC107, 0xFFF44336, 0xFF9C27B0)

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {

        Text("Añadir Evento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hora inicio:")
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Hora fin:")
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { horaFin = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Color:")
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            colores.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(color))
                        .border(
                            if (colorSeleccionado == color) 2.dp else 0.dp,
                            Color.Black,
                            CircleShape
                        )
                        .clickable { colorSeleccionado = color }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selectores para tipo, categoría, prioridad y repetición
        SelectorOpciones("Tipo:", listOf("Evento", "Tarea", "Recordatorio"), tipo) { tipo = it }
        SelectorOpciones("Categoría:", listOf("Trabajo", "Personal", "Salud", "Estudio"), categoria) { categoria = it }
        SelectorOpciones("Prioridad:", (1..5).map { it.toString() }, prioridad.toString()) { prioridad = it.toInt() }
        SelectorOpciones("Repetición:", listOf("Ninguno", "Diario", "Semanal", "Mensual", "Anual"), repeticion) { repeticion = it }

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
                onClick = {
                    // VERIFICACIÓN: Asegurarnos que la fecha es correcta
                    Log.d("FormularioNota", "Guardando nota para fecha: $fecha")

                    val nota = Nota(
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha, // Usar la fecha que viene como parámetro
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        color = colorSeleccionado,
                        tipo = tipo,
                        horaRecordatorio = if (tipo == "Recordatorio") horaInicio else null,
                        repeticion = repeticion,
                        categoria = categoria,
                        prioridad = prioridad
                    )
                    viewModel.insertarNota(nota)
                }
            ) {
                Text("Guardar", color = Color.White)
            }
        }
    }
}

@Composable
fun SelectorOpciones(
    etiqueta: String,
    opciones: List<String>,
    valorActual: String,
    onValorCambiado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            // Botón que muestra el valor actual y abre el menú
            OutlinedButton (
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = valorActual,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir opciones"
                )
            }

            // Menú desplegable
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = opcion,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = {
                            onValorCambiado(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}