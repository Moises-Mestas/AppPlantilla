package com.example.appfirst.ui.screens.Agenda

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Asignatura
import com.example.appfirst.ui.tarea.TareaViewModel
import com.example.appfirst.ui.tarea.rememberTareaVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTareaScreen(
    viewModel: TareaViewModel = rememberTareaVM(),
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val form by viewModel.form.collectAsState()
    val context = LocalContext.current

    var userId by remember { mutableStateOf<Long?>(null) }
    val asignaturas = remember { mutableStateListOf<Asignatura>() }

    // ✅ cargar el userId al entrar
    LaunchedEffect(Unit) {
        val id = UserPrefs.getLoggedUserId(context)
        if (id != null) {
            userId = id
            viewModel.setUserId(id)
            Log.d("FormTareaScreen", "✅ UserId cargado: $id")

            AppDatabase.get(context).asignaturaDao()
                .getAsignaturasByUser(id)
                .collectLatest { lista ->
                    asignaturas.clear()
                    asignaturas.addAll(lista)
                }
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // ---------- Estados de los diálogos ----------
    var showEntregaPicker by remember { mutableStateOf(false) }
    var showRecordatorioPicker by remember { mutableStateOf(false) }

    // ✅ Estados de los pickers
    val entregaPickerState = rememberDatePickerState()
    val recordatorioPickerState = rememberDatePickerState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Botones de navegación y guardar
        Row {
            Button(onClick = { onBack() }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Atrás", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { scope.launch { viewModel.save() } },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Guardar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Título
        OutlinedTextField(
            value = form.titulo,
            onValueChange = { viewModel.onFormChange(titulo = it) },
            label = { Text("Título de la tarea") },
            isError = form.errors.containsKey("titulo"),
            modifier = Modifier.fillMaxWidth()
        )
        form.errors["titulo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- FECHA DE ENTREGA ----------
        OutlinedTextField(
            value = form.fechaEntrega.takeIf { it.isNotBlank() }?.let {
                dateFormatter.format(Date(it.toLong()))
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha entrega") },
            trailingIcon = {
                IconButton(onClick = { showEntregaPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = form.errors.containsKey("fechaEntrega")
        )
        form.errors["fechaEntrega"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- FECHA DE RECORDATORIO ----------
        OutlinedTextField(
            value = form.fechaRecordatorio.takeIf { it.isNotBlank() }?.let {
                dateFormatter.format(Date(it.toLong()))
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha recordatorio") },
            trailingIcon = {
                IconButton(onClick = { showRecordatorioPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = form.errors.containsKey("fechaRecordatorio")
        )
        form.errors["fechaRecordatorio"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Asignatura (dropdown)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = asignaturas.firstOrNull { it.id == form.asignaturaId }?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Asignatura") },
                isError = form.errors.containsKey("asignaturaId"),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                asignaturas.forEach { asig ->
                    DropdownMenuItem(
                        text = { Text(asig.nombre) },
                        onClick = {
                            viewModel.onFormChange(asignaturaId = asig.id)
                            expanded = false
                        }
                    )
                }
            }
        }
        form.errors["asignaturaId"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Nota
        OutlinedTextField(
            value = form.nota,
            onValueChange = { viewModel.onFormChange(nota = it) },
            label = { Text("Nota (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Checkbox: Completada
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = form.completada,
                onCheckedChange = { viewModel.onFormChange(completada = it) }
            )
            Text("¿Completada?")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mensaje general
        form.errors["general"]?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }

    // ---------- Diálogo de FECHA ENTREGA ----------
    if (showEntregaPicker) {
        DatePickerDialog(
            onDismissRequest = { showEntregaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = entregaPickerState.selectedDateMillis
                    if (millis != null) {
                        viewModel.onFormChange(fechaEntrega = millis.toString())
                    }
                    showEntregaPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEntregaPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = entregaPickerState)
        }
    }

    // ---------- Diálogo de FECHA RECORDATORIO ----------
    if (showRecordatorioPicker) {
        DatePickerDialog(
            onDismissRequest = { showRecordatorioPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = recordatorioPickerState.selectedDateMillis
                    if (millis != null) {
                        viewModel.onFormChange(fechaRecordatorio = millis.toString())
                    }
                    showRecordatorioPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showRecordatorioPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = recordatorioPickerState)
        }
    }
}
