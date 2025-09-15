package com.example.appfirst.ui.screens.Agenda

import androidx.compose.runtime.Composable

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Recordatorio
import com.example.appfirst.ui.recordatorio.RecordatorioFormState
import com.example.appfirst.ui.recordatorio.RecordatorioViewModel
import com.example.appfirst.ui.recordatorio.rememberRecordatorioVM
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    var showRecordatorioPicker by remember { mutableStateOf(false) }
    val recordatorioPickerState = rememberDatePickerState()
    var userId by remember { mutableStateOf<Long?>(null) }

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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedColor, onDismissRequest = { expandedColor = false }) {
                        recordatorioVM.coloresDisponibles.forEach { color ->
                            DropdownMenuItem(
                                text = { Text(color) },
                                onClick = {
                                    recordatorioVM.onFormChange(color = color, archivos = emptyList())
                                    expandedColor = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Botones Guardar / Cancelar
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

    // ---------- Diálogo de fecha ----------
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

    // ---------- Pantalla principal con lista ----------
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recordatorios) { recordatorio ->
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = recordatorio.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = "Fecha: ${dateFormatter.format(Date(recordatorio.fechaRecordatorio))}")
            Text(text = "Color: ${recordatorio.color}")
            recordatorio.nota?.let { Text("Nota: $it") }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
