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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Asignatura
import com.example.appfirst.data.local.entity.Examen
import com.example.appfirst.ui.examen.ExamenViewModel
import com.example.appfirst.ui.examen.rememberExamenVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormExamenScreen(
    onBack: () -> Unit = {},
    examenVM: ExamenViewModel = rememberExamenVM()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var examenEditando by remember { mutableStateOf<Examen?>(null) }

    val examenes by examenVM.filteredExamenes.collectAsStateWithLifecycle(initialValue = emptyList())
    val query by examenVM.query.collectAsStateWithLifecycle()
    val form by examenVM.form.collectAsState()

    // Asignaturas del usuario
    var userId by remember { mutableStateOf<Long?>(null) }
    val asignaturas = remember { mutableStateListOf<Asignatura>() }
    var filtroAsignaturaId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        val id = UserPrefs.getLoggedUserId(context)
        if (id != null) {
            userId = id
            examenVM.setUserId(id)
            Log.d("FormExamenScreen", "✅ UserId cargado: $id")

            AppDatabase.get(context).asignaturaDao()
                .getAsignaturasByUser(id)
                .collectLatest { lista ->
                    asignaturas.clear()
                    asignaturas.addAll(lista)
                }
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // Estados de diálogos
    var showExamenPicker by remember { mutableStateOf(false) }
    var showRecordatorioPicker by remember { mutableStateOf(false) }
    var showExamenTimePicker by remember { mutableStateOf(false) }
    var showRecordatorioTimePicker by remember { mutableStateOf(false) }
    val examenPickerState = rememberDatePickerState()
    val recordatorioPickerState = rememberDatePickerState()

    val documentPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val actual = form.archivos.toMutableList()
                actual.add(it.toString())
                examenVM.onFormChange(archivos = actual)
            }
        }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                examenEditando = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (examenEditando == null) "Nuevo examen" else "Editar examen",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = form.titulo,
                    onValueChange = { examenVM.onFormChange(titulo = it) },
                    label = { Text("Título del examen") },
                    isError = form.errors.containsKey("titulo"),
                    modifier = Modifier.fillMaxWidth()
                )
                form.errors["titulo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.fechaExamen.takeIf { it.isNotBlank() }?.let {
                        dateFormatter.format(Date(it.toLong()))
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha examen") },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showExamenPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                            IconButton(onClick = { showExamenTimePicker = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = form.errors.containsKey("fechaExamen")
                )
                form.errors["fechaExamen"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

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
                                    examenVM.onFormChange(asignaturaId = asig.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                var expandedCategoria by remember { mutableStateOf(false) }
                val categorias = listOf("oral", "escrito", "práctico")

                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {
                    OutlinedTextField(
                        value = form.categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    examenVM.onFormChange(categoria = cat)
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.nota,
                    onValueChange = { examenVM.onFormChange(nota = it) },
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
                                    examenVM.onFormChange(archivos = actual)
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
                        scope.launch { examenVM.save() }
                        showBottomSheet = false
                        examenEditando = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        showBottomSheet = false
                        examenEditando = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    if (showExamenPicker) {
        DatePickerDialog(
            onDismissRequest = { showExamenPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = examenPickerState.selectedDateMillis
                    if (millis != null) {
                        examenVM.onFormChange(fechaExamen = millis.toString())
                    }
                    showExamenPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showExamenPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = examenPickerState)
        }
    }

    if (showRecordatorioPicker) {
        DatePickerDialog(
            onDismissRequest = { showRecordatorioPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = recordatorioPickerState.selectedDateMillis
                    if (millis != null) {
                        val examenMillis = form.fechaExamen.toLongOrNull()
                        if (examenMillis != null && millis >= examenMillis) {
                            examenVM.onFormError("fechaRecordatorio", "Debe ser antes de la fecha de examen")
                        } else {
                            examenVM.onFormChange(fechaRecordatorio = millis.toString())
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

    if (showExamenTimePicker) {
        val calendar = Calendar.getInstance()
        val currentMillis = form.fechaExamen.toLongOrNull()
        if (currentMillis != null) calendar.timeInMillis = currentMillis

        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                examenVM.onFormChange(fechaExamen = calendar.timeInMillis.toString())
                showExamenTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
        showExamenTimePicker = false
    }

    if (showRecordatorioTimePicker) {
        val calendar = Calendar.getInstance()
        val currentMillis = form.fechaRecordatorio.toLongOrNull()
        if (currentMillis != null) calendar.timeInMillis = currentMillis

        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                examenVM.onFormChange(fechaRecordatorio = calendar.timeInMillis.toString())
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
                title = { Text("Mis Exámenes") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        examenVM.startCreate()
                        showBottomSheet = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo examen")
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
                onValueChange = { examenVM.setQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar examen") }
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
                val examenesFiltrados = if (filtroAsignaturaId != null) {
                    examenes.filter { it.asignaturaId == filtroAsignaturaId }
                } else examenes

                items(examenesFiltrados) { examen ->
                    ExamenCard(
                        examen = examen,
                        asignaturaNombre = asignaturas.firstOrNull { it.id == examen.asignaturaId }?.nombre,
                        dateFormatter = dateFormatter,
                        onEdit = {
                            examenVM.loadForEdit(examen.id)
                            examenEditando = examen
                            showBottomSheet = true
                        },
                        onDelete = { examenVM.delete(examen.id) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamenCard(
    examen: Examen,
    asignaturaNombre: String?,
    dateFormatter: SimpleDateFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    val diasRestantes = remember {
        val hoy = Date().time
        val diffMillis = examen.fechaExamen - hoy
        (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    val fechaFormato = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
    val horaFormato = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = examen.titulo,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(6.dp))

            Text("📘 Asignatura: ${asignaturaNombre ?: "Sin asignar"}")
            Text("📝 Categoría: ${examen.categoria}")

            Spacer(Modifier.height(8.dp))

            Row {
                Text("📅 Fecha: ${fechaFormato.format(Date(examen.fechaExamen))}")
            }
            Row {
                Text("⏰ Hora: ${horaFormato.format(Date(examen.fechaExamen))}")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "⏳ Faltan $diasRestantes días",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (diasRestantes < 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(8.dp))

            examen.nota?.let {
                Text("⭐ Nota: $it")
            }

            if (examen.archivos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("📂 Archivos:")

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    examen.archivos.forEach { archivo ->
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
                                    Log.e("ExamenCard", "Error abriendo archivo: $archivo", e)
                                }
                            },
                            label = { Text(archivo.substringAfterLast("/")) }
                        )
                    }
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
