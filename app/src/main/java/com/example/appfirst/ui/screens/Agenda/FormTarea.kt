package com.example.appfirst.ui.screens.Agenda

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Asignatura
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.ui.tarea.TareaViewModel
import com.example.appfirst.ui.tarea.rememberTareaVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTareaScreen(
    onBack: () -> Unit = {},
    tareaVM: TareaViewModel = rememberTareaVM()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var tareaEditando by remember { mutableStateOf<Tarea?>(null) }

    val tareas by tareaVM.filteredTareas.collectAsStateWithLifecycle(initialValue = emptyList())
    val query by tareaVM.query.collectAsStateWithLifecycle()
    val form by tareaVM.form.collectAsState()

    // Asignaturas del usuario
    var userId by remember { mutableStateOf<Long?>(null) }
    val asignaturas = remember { mutableStateListOf<Asignatura>() }

    // ✅ cargar userId y asignaturas
    LaunchedEffect(Unit) {
        val id = UserPrefs.getLoggedUserId(context)
        if (id != null) {
            userId = id
            tareaVM.setUserId(id)
            Log.d("FormTareaScreen", "✅ UserId cargado: $id")

            AppDatabase.get(context).asignaturaDao()
                .getAsignaturasByUser(id)
                .collectLatest { lista ->
                    asignaturas.clear()
                    asignaturas.addAll(lista)
                }
        } else {
            Log.e("FormTareaScreen", "⚠️ No se encontró un userId")
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // Estados de diálogos
    var showEntregaPicker by remember { mutableStateOf(false) }
    var showRecordatorioPicker by remember { mutableStateOf(false) }
    val entregaPickerState = rememberDatePickerState()
    val recordatorioPickerState = rememberDatePickerState()

    // 👉 Document Picker
    val archivoUris = remember { mutableStateListOf<String>() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                archivoUris.clear()
                archivoUris.addAll(uris.map { it.toString() })
                tareaVM.onFormChange(archivos = archivoUris.toList())
            }
        }
    )

    // --- Modal Bottom Sheet (Formulario) ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                tareaEditando = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (tareaEditando == null) "Nueva tarea" else "Editar tarea",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(20.dp))

                // ---------- Título ----------
                OutlinedTextField(
                    value = form.titulo,
                    onValueChange = { tareaVM.onFormChange(titulo = it) },
                    label = { Text("Título de la tarea") },
                    isError = form.errors.containsKey("titulo"),
                    modifier = Modifier.fillMaxWidth()
                )
                form.errors["titulo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                // ---------- Fecha de entrega ----------
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

                Spacer(Modifier.height(12.dp))

                // ---------- Fecha de recordatorio ----------
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

                // ---------- Asignatura ----------
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        asignaturas.forEach { asig ->
                            DropdownMenuItem(
                                text = { Text(asig.nombre) },
                                onClick = {
                                    tareaVM.onFormChange(asignaturaId = asig.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ---------- Nota ----------
                OutlinedTextField(
                    value = form.nota,
                    onValueChange = { tareaVM.onFormChange(nota = it) },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // ---------- Archivos ----------
                Text("Archivos adjuntos:")
                archivoUris.forEach { uri ->
                    Text(text = "- $uri", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    launcher.launch(arrayOf("*/*")) // 📂 permite elegir cualquier archivo
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Adjuntar archivos")
                }

                Spacer(Modifier.height(12.dp))

                // ---------- Completada ----------
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = form.completada,
                        onCheckedChange = { tareaVM.onFormChange(completada = it) }
                    )
                    Text("¿Completada?")
                }

                Spacer(Modifier.height(20.dp))

                // Botones Guardar / Cancelar
                Button(
                    onClick = {
                        scope.launch { tareaVM.save() }
                        showBottomSheet = false
                        tareaEditando = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        showBottomSheet = false
                        tareaEditando = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    // ---------- Diálogos de fecha ----------
    if (showEntregaPicker) {
        DatePickerDialog(
            onDismissRequest = { showEntregaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = entregaPickerState.selectedDateMillis
                    if (millis != null) {
                        tareaVM.onFormChange(fechaEntrega = millis.toString())
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

    if (showRecordatorioPicker) {
        DatePickerDialog(
            onDismissRequest = { showRecordatorioPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = recordatorioPickerState.selectedDateMillis
                    if (millis != null) {
                        tareaVM.onFormChange(fechaRecordatorio = millis.toString())
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
                title = { Text("Mis Tareas") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        tareaVM.startCreate()
                        showBottomSheet = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
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
                onValueChange = { tareaVM.setQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar tarea") }
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tareas) { tarea ->
                    TareaCard(
                        tarea = tarea,
                        asignaturaNombre = asignaturas.firstOrNull { it.id == tarea.asignaturaId }?.nombre,
                        dateFormatter = dateFormatter,
                        onEdit = {
                            tareaVM.loadForEdit(tarea.id)
                            tareaEditando = tarea
                            showBottomSheet = true
                        },
                        onDelete = { tareaVM.delete(tarea.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TareaCard(
    tarea: Tarea,
    asignaturaNombre: String?,
    dateFormatter: SimpleDateFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = "Asignatura: ${asignaturaNombre ?: "Sin asignar"}")
            Text(text = "Entrega: ${dateFormatter.format(Date(tarea.fechaEntrega))}")

            if (tarea.archivos.isNotEmpty()) {
                Text("Archivos adjuntos:", style = MaterialTheme.typography.bodySmall)
                tarea.archivos.forEach { file ->
                    Text("- $file", style = MaterialTheme.typography.bodySmall)
                }
            }

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
