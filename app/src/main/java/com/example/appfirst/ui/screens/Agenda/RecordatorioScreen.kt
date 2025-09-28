package com.example.appfirst.ui.screens.Agenda

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Recordatorio
import com.example.appfirst.ui.recordatorio.RecordatorioViewModel
import com.example.appfirst.ui.recordatorio.rememberRecordatorioVM
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecordatorioScreen(
    onBack: () -> Unit = {},
    recordatorioVM: RecordatorioViewModel = rememberRecordatorioVM()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var recordatorioEditando by remember { mutableStateOf<Recordatorio?>(null) }

    val recordatorios by recordatorioVM.filteredRecordatorios.collectAsStateWithLifecycle(initialValue = emptyList())
    val query by recordatorioVM.query.collectAsStateWithLifecycle()
    val form by recordatorioVM.form.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var showRecordatorioPicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val recordatorioPickerState = rememberDatePickerState()
    var userId by remember { mutableStateOf<Long?>(null) }

    var selectedColor by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        val id = UserPrefs.getLoggedUserId(context)
        if (id != null) {
            userId = id
            recordatorioVM.setUserId(id)
            Log.d("RecordatorioScreen", "✅ UserId cargado: $id")
        } else {
            Log.e("RecordatorioScreen", "⚠️ No se encontró un userId")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                recordatorioEditando = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (recordatorioEditando == null) "Nuevo recordatorio" else "Editar recordatorio",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = form.titulo,
                    onValueChange = { recordatorioVM.onFormChange(titulo = it, archivos = emptyList()) },
                    label = { Text("Título del recordatorio") },
                    isError = form.errors.containsKey("titulo"),
                    modifier = Modifier.fillMaxWidth()
                )
                form.errors["titulo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.fechaRecordatorio.takeIf { it.isNotBlank() }?.let {
                        dateFormatter.format(Date(it.toLong()))
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha y hora del recordatorio") },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showRecordatorioPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = form.errors.containsKey("fechaRecordatorio")
                )
                form.errors["fechaRecordatorio"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.nota,
                    onValueChange = { recordatorioVM.onFormChange(nota = it, archivos = emptyList()) },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                var expandedColor by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedColor,
                    onExpandedChange = { expandedColor = !expandedColor }
                ) {
                    OutlinedTextField(
                        value = form.color,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Color") },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(form.color)))
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedColor, onDismissRequest = { expandedColor = false }) {
                        recordatorioVM.coloresDisponibles.forEach { colorHex ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(colorHex)
                                    }
                                },
                                onClick = {
                                    recordatorioVM.onFormChange(color = colorHex, archivos = emptyList())
                                    expandedColor = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        scope.launch { recordatorioVM.save() }
                        showBottomSheet = false
                        recordatorioEditando = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        showBottomSheet = false
                        recordatorioEditando = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    if (showRecordatorioPicker) {
        DatePickerDialog(
            onDismissRequest = { showRecordatorioPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = recordatorioPickerState.selectedDateMillis
                    if (millis != null) {
                        recordatorioVM.onFormChange(fechaRecordatorio = millis.toString(), archivos = emptyList())
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

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        val currentMillis = form.fechaRecordatorio.toLongOrNull()
        if (currentMillis != null) {
            calendar.timeInMillis = currentMillis
        }

        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                recordatorioVM.onFormChange(
                    fechaRecordatorio = calendar.timeInMillis.toString(),
                    archivos = emptyList()
                )
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
        showTimePicker = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recordatorios") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        recordatorioVM.startCreate()
                        showBottomSheet = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo recordatorio")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { recordatorioVM.setQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar recordatorio") }
            )

            Spacer(Modifier.height(12.dp))

            var expandedFilter by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedFilter,
                onExpandedChange = { expandedFilter = !expandedFilter }
            ) {
                OutlinedTextField(
                    value = selectedColor,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por color") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedFilter,
                    onDismissRequest = { expandedFilter = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos") },
                        onClick = {
                            selectedColor = "Todos"
                            expandedFilter = false
                        }
                    )
                    recordatorioVM.coloresDisponibles.forEach { colorHex ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(colorHex)))
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(colorHex)
                                }
                            },
                            onClick = {
                                selectedColor = colorHex
                                expandedFilter = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredList = if (selectedColor == "Todos") {
                    recordatorios
                } else {
                    recordatorios.filter { it.color == selectedColor }
                }

                items(filteredList) { recordatorio ->
                    RecordatorioCard(
                        recordatorio = recordatorio,
                        dateFormatter = dateFormatter,
                        onEdit = {
                            recordatorioVM.loadForEdit(recordatorio.id)
                            recordatorioEditando = recordatorio
                            showBottomSheet = true
                        },
                        onDelete = { recordatorioVM.delete(recordatorio.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecordatorioCard(
    recordatorio: Recordatorio,
    dateFormatter: SimpleDateFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(recordatorio.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val fecha = Date(recordatorio.fechaRecordatorio)

    val diasRestantes = remember(recordatorio.fechaRecordatorio) {
        val hoy = System.currentTimeMillis()
        val diff = fecha.time - hoy
        (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            Text("📅 Fecha: ${dateFormatter.format(fecha)}", color = Color.White)
            Text("⏰ Hora: ${timeFormatter.format(fecha)}", color = Color.White)

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (diasRestantes >= 0) {
                    "⏳ Faltan $diasRestantes días"
                } else {
                    "✅ Ya pasó hace ${-diasRestantes} días"
                },
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )

            recordatorio.nota?.let {
                Spacer(Modifier.height(4.dp))
                Text("📝 Nota: $it", color = Color.White)
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                }
            }
        }
    }
}
