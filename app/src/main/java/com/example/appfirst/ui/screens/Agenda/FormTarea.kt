package com.example.appfirst.ui.screens.Agenda

import android.app.TimePickerDialog
import android.content.Intent
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    // Asignaturas
    var userId by remember { mutableStateOf<Long?>(null) }
    val asignaturas = remember { mutableStateListOf<Asignatura>() }
    var filtroAsignaturaId by remember { mutableStateOf<Long?>(null) }

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
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    var showEntregaPicker by remember { mutableStateOf(false) }
    var showRecordatorioPicker by remember { mutableStateOf(false) }
    var showEntregaTimePicker by remember { mutableStateOf(false) }
    var showRecordatorioTimePicker by remember { mutableStateOf(false) }
    val entregaPickerState = rememberDatePickerState()
    val recordatorioPickerState = rememberDatePickerState()

    val documentPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val actual = form.archivos.toMutableList()
                actual.add(it.toString())
                tareaVM.onFormChange(archivos = actual)
            }
        }

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

                OutlinedTextField(
                    value = form.titulo,
                    onValueChange = { tareaVM.onFormChange(titulo = it) },
                    label = { Text("Título de la tarea") },
                    isError = form.errors.containsKey("titulo"),
                    modifier = Modifier.fillMaxWidth()
                )
                form.errors["titulo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.fechaEntrega.takeIf { it.isNotBlank() }?.let {
                        dateFormatter.format(Date(it.toLong()))
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha entrega") },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showEntregaPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                            IconButton(onClick = { showEntregaTimePicker = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = form.errors.containsKey("fechaEntrega")
                )
                form.errors["fechaEntrega"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.fechaRecordatorio.takeIf { it.isNotBlank() }?.let {
                        dateFormatter.format(Date(it.toLong()))
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha recordatorio") },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showRecordatorioPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                            IconButton(onClick = { showRecordatorioTimePicker = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = form.errors.containsKey("fechaRecordatorio")
                )
                form.errors["fechaRecordatorio"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

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

                OutlinedTextField(
                    value = form.nota,
                    onValueChange = { tareaVM.onFormChange(nota = it) },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text("Archivos adjuntos")
                Spacer(Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    form.archivos.forEach { archivo ->
                        AssistChip(
                            onClick = { /* abrir archivo */ },
                            label = { Text(archivo.substringAfterLast("/")) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val actual = form.archivos.toMutableList()
                                    actual.remove(archivo)
                                    tareaVM.onFormChange(archivos = actual)
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Quitar archivo")
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { documentPickerLauncher.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar archivo")
                }


                Spacer(Modifier.height(20.dp))

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
                        val entregaMillis = form.fechaEntrega.toLongOrNull()
                        if (entregaMillis != null && millis >= entregaMillis) {
                            tareaVM.onFormError("fechaRecordatorio", "Debe ser antes de la fecha de entrega")
                        } else {
                            tareaVM.onFormChange(fechaRecordatorio = millis.toString())
                        }
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

    if (showEntregaTimePicker) {
        val calendar = Calendar.getInstance()
        val currentMillis = form.fechaEntrega.toLongOrNull()
        if (currentMillis != null) calendar.timeInMillis = currentMillis

        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                tareaVM.onFormChange(fechaEntrega = calendar.timeInMillis.toString())
                showEntregaTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
        showEntregaTimePicker = false
    }

    if (showRecordatorioTimePicker) {
        val calendar = Calendar.getInstance()
        val currentMillis = form.fechaRecordatorio.toLongOrNull()
        if (currentMillis != null) calendar.timeInMillis = currentMillis

        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                tareaVM.onFormChange(fechaRecordatorio = calendar.timeInMillis.toString())
                showRecordatorioTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
        showRecordatorioTimePicker = false
    }

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

            var expandedFiltro by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedFiltro,
                onExpandedChange = { expandedFiltro = !expandedFiltro }
            ) {
                OutlinedTextField(
                    value = asignaturas.firstOrNull { it.id == filtroAsignaturaId }?.nombre ?: "Todas",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por asignatura") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedFiltro, onDismissRequest = { expandedFiltro = false }) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            filtroAsignaturaId = null
                            expandedFiltro = false
                        }
                    )
                    asignaturas.forEach { asig ->
                        DropdownMenuItem(
                            text = { Text(asig.nombre) },
                            onClick = {
                                filtroAsignaturaId = asig.id
                                expandedFiltro = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val tareasFiltradas = if (filtroAsignaturaId != null) {
                    tareas.filter { it.asignaturaId == filtroAsignaturaId }
                } else tareas

                items(tareasFiltradas) { tarea ->
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TareaCard(
    tarea: Tarea,
    asignaturaNombre: String?,
    dateFormatter: SimpleDateFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    val diasRestantes = remember {
        val hoy = Date().time
        val diffMillis = tarea.fechaEntrega - hoy
        (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    val fechaFormato = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val horaFormato = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(text = tarea.titulo, style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(6.dp))

            Text("📘 Asignatura: ${asignaturaNombre ?: "Sin asignar"}")
            Spacer(Modifier.height(4.dp))
            Text("📅 Fecha: ${fechaFormato.format(Date(tarea.fechaEntrega))}")
            Text("⏰ Hora: ${horaFormato.format(Date(tarea.fechaEntrega))}")

            Spacer(Modifier.height(8.dp))

            Text(
                text = "⏳ Faltan $diasRestantes días",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (diasRestantes < 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(8.dp))

            tarea.nota?.let { Text("⭐ Nota: $it") }

            if (tarea.archivos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("📂 Archivos:")

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tarea.archivos.forEach { archivo ->
                        AssistChip(
                            onClick = {
                                try {
                                    val uri = Uri.parse(archivo)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = uri
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Log.e("TareaCard", "Error abriendo archivo: $archivo", e)
                                }
                            },
                            label = { Text(archivo.substringAfterLast("/")) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
            }
        }
    }
}

