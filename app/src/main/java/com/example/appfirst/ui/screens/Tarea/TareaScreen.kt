package com.example.appfirst.ui.screens.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.ui.tarea.TareaViewModel
import com.example.appfirst.ui.tarea.rememberTareaVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen() {
    val viewModel = rememberTareaVM()
    val tareas by viewModel.tareas.collectAsState()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForm by remember { mutableStateOf(false) }

    // Obtener userId automáticamente al iniciar
    LaunchedEffect(Unit) {
        try {
            val userDao = AppDatabase.get(context).userDao()
            val userId = withContext(Dispatchers.IO) {
                val userEmail = UserPrefs.getLoggedUserEmail(context)
                val users = userDao.getAllUsers().first()
                users.firstOrNull { it.email == userEmail }?.id
            }
            if (userId != null) {
                viewModel.setUserId(userId)
            } else {
                errorMessage = "Usuario no encontrado"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis Tareas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(if (showForm) "Cerrar formulario" else "Agregar tarea")
        }

        if (showForm) {
            TareaFormScreen(
                viewModel = viewModel,
                onSuccess = { showForm = false }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
        } else if (tareas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay tareas. ¡Agrega una desde el formulario!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareas) { tarea ->
                    TareaItemSimple(tarea = tarea)
                }
            }
        }
    }
}

@Composable
fun TareaItemSimple(tarea: Tarea) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tarea.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
            )
            // Igual que antes, simulamos lista. Lo ideal: traer de BD/DAO
            val asignaturas = mapOf(
                1L to "Matemáticas",
                2L to "Historia",
                3L to "Programación"
            )

            val asignaturaNombre = asignaturas[tarea.asignaturaId] ?: "Sin asignatura"

            Text(
                text = "📚 $asignaturaNombre",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "📅 Entrega: ${formatFecha(tarea.fechaEntrega)}",
                fontSize = 12.sp
            )
            Text(
                text = "⏰ Recordatorio: ${formatFecha(tarea.fechaRecordatorio)}",
                fontSize = 12.sp
            )
            Text(
                text = if (tarea.completada) "✅ Completada" else "⏳ Pendiente",
                fontSize = 12.sp,
                color = if (tarea.completada) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
            tarea.nota?.takeIf { it.isNotBlank() }?.let { nota ->
                Text(
                    text = "📝 $nota",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaFormScreen(
    viewModel: TareaViewModel,
    onSuccess: () -> Unit
) {
    val form by viewModel.form.collectAsState()
    val message by viewModel.message.collectAsState()
    val navigateToSuccess by viewModel.navigateToSuccess.collectAsState()

    // Estados para pickers
    var showEntregaDatePicker by remember { mutableStateOf(false) }
    var showEntregaTimePicker by remember { mutableStateOf(false) }
    var showRecordatorioDatePicker by remember { mutableStateOf(false) }
    var showRecordatorioTimePicker by remember { mutableStateOf(false) }

    val entregaMillis = form.fechaEntrega.toLongOrNull() ?: System.currentTimeMillis()
    val recordatorioMillis = form.fechaRecordatorio.toLongOrNull() ?: System.currentTimeMillis()

    LaunchedEffect(navigateToSuccess) {
        if (navigateToSuccess != null) {
            onSuccess()
            viewModel.resetNavigation()
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = form.titulo,
            onValueChange = { viewModel.onFormChange(titulo = it) },
            label = { Text("Título") },
            isError = form.errors.containsKey("titulo"),
            modifier = Modifier.fillMaxWidth()
        )
        form.errors["titulo"]?.let { Text(it, color = Color.Red) }

        Spacer(Modifier.height(8.dp))
        Text("Fecha de entrega", fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatFecha(entregaMillis),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { showEntregaDatePicker = true }) {
                Text("Fecha")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { showEntregaTimePicker = true }) {
                Text("Hora")
            }
        }
        form.errors["fechaEntrega"]?.let { Text(it, color = Color.Red) }

        Spacer(Modifier.height(8.dp))
        Text("Fecha de recordatorio", fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatFecha(recordatorioMillis),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { showRecordatorioDatePicker = true }) {
                Text("Fecha")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { showRecordatorioTimePicker = true }) {
                Text("Hora")
            }
        }
        form.errors["fechaRecordatorio"]?.let { Text(it, color = Color.Red) }

// Lista de asignaturas simulada (ideal: traer de BD)
        val asignaturas = listOf(
            1L to "Matemáticas",
            2L to "Historia",
            3L to "Programación"
        )

        var expanded by remember { mutableStateOf(false) }
        val selectedName = asignaturas.firstOrNull { it.first == form.asignaturaId }?.second ?: ""

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Asignatura") },
                isError = form.errors.containsKey("asignaturaId"),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                asignaturas.forEach { (id, nombre) ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = {
                            viewModel.onFormChange(asignaturaId = id)
                            expanded = false
                        }
                    )
                }
            }
        }
        form.errors["asignaturaId"]?.let { Text(it, color = Color.Red) }

        OutlinedTextField(
            value = form.nota,
            onValueChange = { viewModel.onFormChange(nota = it) },
            label = { Text("Nota (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        form.errors["general"]?.let { Text(it, color = Color.Red) }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        message?.let {
            Text(it, color = Color.Green)
            LaunchedEffect(it) {
                kotlinx.coroutines.delay(2000)
                viewModel.clearMessage()
            }
        }
    }

    // DatePicker y TimePicker para entrega
    if (showEntregaDatePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = entregaMillis }
        DatePickerDialog(
            initialDate = calendar,
            onDateSelected = { year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                viewModel.onFormChange(fechaEntrega = calendar.timeInMillis.toString())
            },
            onDismiss = { showEntregaDatePicker = false }
        )
    }
    if (showEntregaTimePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = entregaMillis }
        TimePickerDialog(
            initialTime = calendar,
            onTimeSelected = { hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                viewModel.onFormChange(fechaEntrega = calendar.timeInMillis.toString())
            },
            onDismiss = { showEntregaTimePicker = false }
        )
    }
    // DatePicker y TimePicker para recordatorio
    if (showRecordatorioDatePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = recordatorioMillis }
        DatePickerDialog(
            initialDate = calendar,
            onDateSelected = { year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                viewModel.onFormChange(fechaRecordatorio = calendar.timeInMillis.toString())
            },
            onDismiss = { showRecordatorioDatePicker = false }
        )
    }
    if (showRecordatorioTimePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = recordatorioMillis }
        TimePickerDialog(
            initialTime = calendar,
            onTimeSelected = { hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                viewModel.onFormChange(fechaRecordatorio = calendar.timeInMillis.toString())
            },
            onDismiss = { showRecordatorioTimePicker = false }
        )
    }
}

// --- Utilidades para DatePicker y TimePicker Compose ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Calendar,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.timeInMillis
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    onDateSelected(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TimePickerDialog(
    initialTime: Calendar,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialTime.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(initialTime.get(Calendar.MINUTE)) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(hour, minute)
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Selecciona hora") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NumberPicker(
                    value = hour,
                    range = 0..23,
                    onValueChange = { hour = it },
                    label = "Hora"
                )
                Spacer(Modifier.width(16.dp))
                NumberPicker(
                    value = minute,
                    range = 0..59,
                    onValueChange = { minute = it },
                    label = "Min"
                )
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
                Text("-")
            }
            Text(
                text = value.toString().padStart(2, '0'),
                fontSize = 24.sp,
                modifier = Modifier.width(32.dp),
            )
            IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
                Text("+")
            }
        }
    }
}

fun formatFecha(timestamp: Long): String {
    return try {
        android.text.format.DateFormat.format("dd/MM/yy HH:mm", Date(timestamp)).toString()
    } catch (e: Exception) {
        "Fecha inválida"
    }
}