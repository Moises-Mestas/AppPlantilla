package com.example.appfirst.ui.screens.Agenda

import androidx.compose.runtime.Composable
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

    // ✅ cargar userId y asignaturas
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
        } else {
            Log.e("FormExamenScreen", "⚠️ No se encontró un userId")
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // Estados de diálogos
    var showExamenPicker by remember { mutableStateOf(false) }
    var showRecordatorioPicker by remember { mutableStateOf(false) }
    val examenPickerState = rememberDatePickerState()
    val recordatorioPickerState = rememberDatePickerState()

    // ✅ Document picker
    val documentPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val actual = form.archivos.toMutableList()
                actual.add(it.toString())
                examenVM.onFormChange(archivos = actual)
            }
        }

    // --- Modal Bottom Sheet (Formulario) ---
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

                // ---------- Título ----------
                OutlinedTextField(
                    value = form.titulo,
                    onValueChange = { examenVM.onFormChange(titulo = it) },
                    label = { Text("Título del examen") },
                    isError = form.errors.containsKey("titulo"),
                    modifier = Modifier.fillMaxWidth()
                )
                form.errors["titulo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(12.dp))

                // ---------- Fecha de examen ----------
                OutlinedTextField(
                    value = form.fechaExamen.takeIf { it.isNotBlank() }?.let {
                        dateFormatter.format(Date(it.toLong()))
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha examen") },
                    trailingIcon = {
                        IconButton(onClick = { showExamenPicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = form.errors.containsKey("fechaExamen")
                )
                form.errors["fechaExamen"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

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
                                    examenVM.onFormChange(asignaturaId = asig.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ---------- Categoría ----------
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

                // ---------- Nota ----------
                OutlinedTextField(
                    value = form.nota,
                    onValueChange = { examenVM.onFormChange(nota = it) },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // ---------- Archivos con picker ----------
                Text("Archivos adjuntos")
                Spacer(Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    form.archivos.forEach { archivo ->
                        AssistChip(
                            onClick = { /* podrías abrir el archivo */ },
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

                // Botones Guardar / Cancelar
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

    // ---------- Diálogos de fecha ----------
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
                        examenVM.onFormChange(fechaRecordatorio = millis.toString())
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

            // 🔎 Filtro por asignatura
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

@Composable
fun ExamenCard(
    examen: Examen,
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
            Text(text = examen.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = "Asignatura: ${asignaturaNombre ?: "Sin asignar"}")
            Text(text = "Categoría: ${examen.categoria}")
            Text(text = "Fecha examen: ${dateFormatter.format(Date(examen.fechaExamen))}")
            examen.nota?.let { Text("Nota: $it") }
            if (examen.archivos.isNotEmpty()) {
                Text("📂 Archivos:")
                examen.archivos.forEach { archivo ->
                    Text("   - ${archivo.substringAfterLast("/")} ")
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
