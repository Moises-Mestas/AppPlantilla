package com.example.appfirst.ui.screens.Agenda

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.data.local.entity.Examen
import com.example.appfirst.data.local.entity.Recordatorio
import com.example.appfirst.ui.screens.home.NavItem
import com.example.appfirst.ui.tarea.rememberTareaVM
import com.example.appfirst.ui.examen.rememberExamenVM
import com.example.appfirst.ui.recordatorio.rememberRecordatorioVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// ---------- ITEMS SIMPLES ----------

@Composable
fun TareaItemSimpleAgenda(tarea: Tarea, esPasada: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                esPasada -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                tarea.completada -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tarea.titulo,
                    fontWeight = FontWeight.Bold,
                    color = if (esPasada) Color.Gray
                    else if (tarea.completada) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (esPasada) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )
                if (esPasada) {
                    Text("Finalizado", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            tarea.nota?.takeIf { it.isNotBlank() }?.let { nota ->
                Text(
                    text = nota,
                    fontSize = 12.sp,
                    color = if (esPasada) Color.Gray else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ExamenItemSimpleAgenda(examen: Examen, esPasado: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esPasado) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = examen.titulo,
                    fontWeight = FontWeight.Bold,
                    color = if (esPasado) Color.Gray else MaterialTheme.colorScheme.primary,
                    textDecoration = if (esPasado) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )
                if (esPasado) {
                    Text("Finalizado", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            examen.nota?.takeIf { it.isNotBlank() }?.let { nota ->
                Text("Nota: $nota", fontSize = 12.sp, color = if (esPasado) Color.Gray else MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun RecordatorioItemSimpleAgenda(recordatorio: Recordatorio, esPasado: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esPasado) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = recordatorio.titulo,
                    fontWeight = FontWeight.Bold,
                    color = if (esPasado) Color.Gray else MaterialTheme.colorScheme.primary,
                    textDecoration = if (esPasado) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )
                if (esPasado) {
                    Text("Finalizado", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            recordatorio.color?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(desc, fontSize = 12.sp, color = if (esPasado) Color.Gray else MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

// ---------- AGENDA SCREEN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    navigateToInicio: () -> Unit = {},
    navigateToAhorros: () -> Unit = {},
    navigateTotarea: () -> Unit = {},
    navigateToCalendario: () -> Unit = {},
    navigateToAmigos: () -> Unit = {},
    navigateToAjustes: () -> Unit = {},
    navigateToSalir: () -> Unit = {},
    navigateToFormTarea: () -> Unit = {},
    navigatetoAsignatura: () -> Unit= {},
    navigateToExamen: () -> Unit = {},
    navigateToRecordatorio: () -> Unit = {},
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableStateOf(2) }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home, navigateToInicio),
        NavItem("Ahorros", Icons.Default.Add, navigateToAhorros),
        NavItem("Notas", Icons.Default.AccountBox, navigateTotarea),
        NavItem("Calendario", Icons.Default.DateRange, navigateToCalendario),
        NavItem("Amigos", Icons.Default.Face, navigateToAmigos)
    )

    val drawerExtraItems = listOf(
        NavItem("Ajustes", Icons.Default.Settings, navigateToAjustes),
        NavItem("Salir", Icons.Default.ExitToApp, navigateToSalir)
    )

    val tareaVM = rememberTareaVM()
    val tareas by tareaVM.tareas.collectAsState()

    val examenVM = rememberExamenVM()
    val examenes by examenVM.examenes.collectAsState()

    val recordatorioVM = rememberRecordatorioVM()
    val recordatorios by recordatorioVM.recordatorios.collectAsState()

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    // Estado para el FAB expandido
    var fabExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val userDao = AppDatabase.get(context).userDao()
        val userId = withContext(Dispatchers.IO) {
            val userEmail = UserPrefs.getLoggedUserEmail(context)
            val users = userDao.getAllUsers().first()
            users.firstOrNull { it.email == userEmail }?.id
        }
        if (userId != null) {
            tareaVM.setUserId(userId)
            examenVM.setUserId(userId)
            recordatorioVM.setUserId(userId)
        }
        isLoading = false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                navItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            scope.launch { drawerState.close() }
                            item.onClick()
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Divider(Modifier.padding(vertical = 8.dp))
                drawerExtraItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            item.onClick()
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Tareas") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                item.onClick()
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            },
            floatingActionButton = {
                Box {
                    if (fabExpanded) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            FloatingActionButton(
                                onClick = { navigateToFormTarea(); fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Nueva Tarea")
                            }

                            FloatingActionButton(
                                onClick = { navigateToFormTarea(); fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Nueva Tarea")
                            }
                            FloatingActionButton(
                                onClick = { navigateToExamen(); fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Default.AccountBox, contentDescription = "Examen")
                            }
                            FloatingActionButton(
                                onClick = { navigateToRecordatorio(); fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Recordatorio")
                            }
                        }
                    }
                    FloatingActionButton(
                        onClick = { fabExpanded = !fabExpanded }
                    ) {
                        Icon(
                            imageVector = if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (fabExpanded) "Cerrar" else "Agregar"
                        )
                    }
                }
            }
        ) { innerPadding ->

            // ----------- CONTENIDO PRINCIPAL ----------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // botones de navegación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(onClick = { }) { Text("Horario", fontSize = 12.sp, maxLines = 1) }
                    ElevatedButton(onClick = { }) { Text("Agenda", fontSize = 12.sp, maxLines = 1) }
                    ElevatedButton(onClick = { }) { Text("Calendario", fontSize = 12.sp, maxLines = 1) }
                    ElevatedButton(onClick = { navigatetoAsignatura() }) { Text("Asignatura", fontSize = 12.sp, maxLines = 1) }
                }

                // --- scroll vertical semana ---
                val dateFormat = remember { SimpleDateFormat("EEEE", Locale("es", "ES")) }
                val dateFormatFull = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }
                val dateFormatCompare = remember { SimpleDateFormat("yyyyMMdd", Locale("es", "ES")) }
                val now = remember {
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(7) { i ->
                        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }
                        val titulo = when (i) {
                            0 -> "Hoy"
                            1 -> "Mañana"
                            else -> dateFormat.format(cal.time).replaceFirstChar { it.uppercase() }
                        }
                        val fecha = dateFormatFull.format(cal.time)
                        val fechaCompare = dateFormatCompare.format(cal.time)

                        val tareasDelDia = tareas.filter {
                            dateFormatCompare.format(Date(it.fechaEntrega)) == fechaCompare
                        }
                        val examenesDelDia = examenes.filter {
                            dateFormatCompare.format(Date(it.fechaExamen)) == fechaCompare
                        }
                        val recordatoriosDelDia = recordatorios.filter {
                            dateFormatCompare.format(Date(it.fechaRecordatorio)) == fechaCompare
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = titulo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = fecha,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                if (tareasDelDia.isEmpty() && examenesDelDia.isEmpty() && recordatoriosDelDia.isEmpty()) {
                                    Text("No hay eventos", fontSize = 14.sp)
                                } else {
                                    Column {
                                        tareasDelDia.forEach { tarea ->
                                            val esPasada = tarea.fechaEntrega < now
                                            TareaItemSimpleAgenda(tarea, esPasada)
                                        }
                                        examenesDelDia.forEach { examen ->
                                            val esPasado = examen.fechaExamen < now
                                            ExamenItemSimpleAgenda(examen, esPasado)
                                        }
                                        recordatoriosDelDia.forEach { recordatorio ->
                                            val esPasado = recordatorio.fechaRecordatorio < now
                                            RecordatorioItemSimpleAgenda(recordatorio, esPasado)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
