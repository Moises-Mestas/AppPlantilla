package com.example.appfirst.ui.screens.calendar

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appfirst.ui.screens.calendar.elementos.CalendarViewModel
import com.example.appfirst.ui.screens.calendar.elementos.CalendarViewModelFactory
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModel
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModelFactory
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaNota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.data.local.entity.Examen
import com.example.appfirst.data.local.entity.Recordatorio
import com.example.appfirst.data.local.entity.Asignatura
import com.example.appfirst.ui.tarea.rememberTareaVM
import com.example.appfirst.ui.examen.rememberExamenVM
import com.example.appfirst.ui.recordatorio.rememberRecordatorioVM
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaDetallesDiaScreen(
    fecha: String,
    navController: NavController,
    onBackToCalendario: () -> Unit,
) {
    val calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    val context = LocalContext.current
    val viewModel: NotaViewModel = viewModel(
        factory = NotaViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val notasState by viewModel.notasState.collectAsState()

    val tareaVM = rememberTareaVM()
    val tareas by tareaVM.tareas.collectAsState()
    val examenVM = rememberExamenVM()
    val examenes by examenVM.examenes.collectAsState()
    val recordatorioVM = rememberRecordatorioVM()
    val recordatorios by recordatorioVM.recordatorios.collectAsState()

    val asignaturas = remember { mutableStateListOf<Asignatura>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = withContext(Dispatchers.IO) {
            val userEmail = UserPrefs.getLoggedUserEmail(context)
            val userDao = AppDatabase.get(context).userDao()
            val users = userDao.getAllUsers().first()
            users.firstOrNull { it.email == userEmail }?.id ?: 0L
        }

        if (userId != 0L) {
            calendarViewModel.cargarMovimientos(userId)
            tareaVM.setUserId(userId)
            examenVM.setUserId(userId)
            recordatorioVM.setUserId(userId)

            AppDatabase.get(context).asignaturaDao().getAsignaturasByUser(userId).collect { listaEntity ->
                asignaturas.clear()
                asignaturas.addAll(listaEntity)
            }
        }
        isLoading = false
    }

    LaunchedEffect(fecha) {
        if (fecha.isNotEmpty()) {
            viewModel.cargarNotasPorFecha(fecha)
        }
    }

    var seccionActiva by remember { mutableStateOf("Eventos") }

    fun navigateToDetails(fecha: String) {
        navController.navigate("detalles-dia/$fecha")
    }

    fun navigateToEditNota(notaId: Int) {
        navController.navigate("editar-nota/$notaId")
    }

    fun navigateToAddNota() {
        navController.navigate("nueva-nota/$fecha")
    }

    fun eliminarNota(id: Int) {
        viewModel.eliminarNota(id)
    }

    val dateFormatCompare = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }
    val fechaCompare = dateFormatCompare.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fecha) ?: Date())

    val tareasDelDia = tareas.filter {
        dateFormatCompare.format(Date(it.fechaEntrega)) == fechaCompare
    }

    val examenesDelDia = examenes.filter {
        dateFormatCompare.format(Date(it.fechaExamen)) == fechaCompare
    }

    val recordatoriosDelDia = recordatorios.filter {
        dateFormatCompare.format(Date(it.fechaRecordatorio)) == fechaCompare
    }

    val asignaturaMap = asignaturas.associateBy { it.id }

    val hayEventosAgenda = tareasDelDia.isNotEmpty() || examenesDelDia.isNotEmpty() || recordatoriosDelDia.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles del día",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToCalendario) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al calendario"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToAddNota() }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir evento")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatearFechaDetalles(fecha),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${notasState.size + tareasDelDia.size + examenesDelDia.size + recordatoriosDelDia.size} evento(s) programado(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = seccionActiva == "Eventos",
                    onClick = { seccionActiva = "Eventos" },
                    label = { Text("Eventos") },
                    leadingIcon = {
                        if (seccionActiva == "Eventos") {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = seccionActiva == "Movimientos",
                    onClick = { seccionActiva = "Movimientos" },
                    label = { Text("Movimientos") },
                    leadingIcon = {
                        if (seccionActiva == "Movimientos") {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            when (seccionActiva) {
                "Eventos" -> {
                    if (notasState.isEmpty() && !hayEventosAgenda) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Sin eventos",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No hay eventos para este día",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Haz clic en el botón + para agregar uno",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { navigateToAddNota() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Añadir",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Crear primer evento")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notasState) { nota ->
                                TarjetaNota(
                                    nota = nota,
                                    onEditar = { navigateToEditNota(nota.id) },
                                    onEliminar = { eliminarNota(nota.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            items(tareasDelDia) { tarea ->
                                TareaItemCalendario(
                                    tarea = tarea,
                                    asignaturaMap = asignaturaMap
                                )
                            }

                            items(examenesDelDia) { examen ->
                                ExamenItemCalendario(
                                    examen = examen,
                                    asignaturaMap = asignaturaMap
                                )
                            }

                            items(recordatoriosDelDia) { recordatorio ->
                                RecordatorioItemCalendario(
                                    recordatorio = recordatorio
                                )
                            }
                        }
                    }
                }
                "Movimientos" -> {
                    SeccionMovimientos(fecha = fecha, viewModel = calendarViewModel)
                }
            }
        }
    }
}

@Composable
fun TareaItemCalendario(
    tarea: Tarea,
    asignaturaMap: Map<Long, Asignatura>
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(tarea.fechaEntrega)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Tarea",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tarea.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                tarea.asignaturaId?.let { id ->
                    asignaturaMap[id]?.let { asignatura ->
                        Text(
                            "📘 ${asignatura.nombre}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Text(
                    "⏰ ${timeFormatter.format(fecha)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (tarea.completada) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completada",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ExamenItemCalendario(
    examen: Examen,
    asignaturaMap: Map<Long, Asignatura>
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(examen.fechaExamen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Examen",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    examen.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                examen.asignaturaId?.let { id ->
                    asignaturaMap[id]?.let { asignatura ->
                        Text(
                            "📘 ${asignatura.nombre}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Text(
                    "⏰ ${timeFormatter.format(fecha)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            examen.nota?.takeIf { it.isNotBlank() }?.let {
                Text(
                    "Nota: $it",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RecordatorioItemCalendario(recordatorio: Recordatorio) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(recordatorio.fechaRecordatorio)
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(recordatorio.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Recordatorio",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    recordatorio.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Text(
                    "⏰ ${timeFormatter.format(fecha)}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun formatearFechaDetalles(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatoEntrada.parse(fecha)
        val localeEspanol = Locale("es", "ES")
        val formatoSalida = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", localeEspanol)
        val fechaFormateada = formatoSalida.format(date ?: return fecha)
        fechaFormateada.split(" ").joinToString(" ") { palabra ->
            palabra.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(localeEspanol) else it.toString()
            }
        }
    } catch (e: Exception) {
        fecha
    }
}

@Composable
fun SeccionMovimientos(
    fecha: String,
    viewModel: CalendarViewModel
) {
    var movimientosDelDia by remember { mutableStateOf<List<com.example.appfirst.data.local.entity.Ingreso>>(emptyList()) }
    var balance by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(fecha) {
        isLoading = true
        errorMessage = null
        try {
            val userId = withContext(Dispatchers.IO) {
                val userEmail = com.example.appfirst.data.datastore.UserPrefs.getLoggedUserEmail(context)
                val userDao = com.example.appfirst.data.local.AppDatabase.get(context).userDao()
                val users = userDao.getAllUsers().first()
                users.firstOrNull { it.email == userEmail }?.id ?: 0L
            }

            if (userId != 0L) {
                movimientosDelDia = viewModel.getMovimientosDelDia(userId, fecha)
                balance = viewModel.getBalanceDelDia(userId, fecha)
            } else {
                errorMessage = "Usuario no encontrado"
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar movimientos: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column {
        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Balance del día: S/ ${String.format("%.2f", balance)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${movimientosDelDia.size} movimiento(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Movimientos del día:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (movimientosDelDia.isEmpty()) {
                Text(
                    "No hay movimientos para este día",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    movimientosDelDia.forEach { movimiento ->
                        val esIngreso = movimiento.monto >= 0
                        val signo = if (esIngreso) "+" else "-"
                        val color = if (esIngreso) Color(0xFF4CAF50) else Color(0xFFF44336)
                        val tipo = if (esIngreso) "Ingreso" else "Gasto"

                        Text(
                            "$signo S/ ${"%.2f".format(kotlin.math.abs(movimiento.monto))} " +
                                    "($tipo - ${movimiento.descripcion}) - ${movimiento.depositadoEn}",
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = color
                        )
                    }
                }
            }
        }
    }
}