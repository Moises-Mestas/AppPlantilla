package com.example.appfirst.ui.screens.Agenda

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.core.navigation.Asignatura
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.data.local.entity.Examen
import com.example.appfirst.data.local.entity.Recordatorio
import com.example.appfirst.ui.screens.home.NavItem
import com.example.appfirst.ui.screens.home.NavDestination
import com.example.appfirst.ui.tarea.rememberTareaVM
import com.example.appfirst.ui.examen.rememberExamenVM
import com.example.appfirst.ui.recordatorio.rememberRecordatorioVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TareaItemSimpleAgenda(
    tarea: Tarea,
    esPasada: Boolean,
    asignaturaMap: Map<Long, com.example.appfirst.data.local.entity.Asignatura>
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(tarea.fechaEntrega)
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                tarea.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (esPasada) Color.Gray else MaterialTheme.colorScheme.primary
            )

            tarea.asignaturaId?.let { id ->
                asignaturaMap[id]?.let { asignatura ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "📘 ${asignatura.nombre}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏰ ${timeFormatter.format(fecha)}", fontSize = 14.sp)
                if (tarea.completada) AssistChip(onClick = {}, label = { Text("Completada") })
            }

            tarea.nota?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text("📝 $it", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }

            if (tarea.archivos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("📂 Archivos:", fontWeight = FontWeight.Medium)

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
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamenItemSimpleAgenda(
    examen: Examen,
    esPasado: Boolean,
    asignaturaMap: Map<Long, com.example.appfirst.data.local.entity.Asignatura>
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(examen.fechaExamen)
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                examen.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (esPasado) Color.Gray else MaterialTheme.colorScheme.primary
            )

            examen.asignaturaId?.let { id ->
                asignaturaMap[id]?.let { asignatura ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "📘 ${asignatura.nombre}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏰ ${timeFormatter.format(fecha)}", fontSize = 14.sp)
                if (examen.nota?.isNotBlank() == true) {
                    AssistChip(onClick = {}, label = { Text("Nota: ${examen.nota}") })
                }
            }

            examen.nota?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text("📝 $it", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }

            if (examen.archivos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("📂 Archivos:", fontWeight = FontWeight.Medium)

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
        }
    }
}



@Composable
fun RecordatorioItemSimpleAgenda(recordatorio: Recordatorio, esPasado: Boolean) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(recordatorio.fechaRecordatorio)
    val backgroundColor = try { Color(android.graphics.Color.parseColor(recordatorio.color)) } catch (e: Exception) { MaterialTheme.colorScheme.surfaceVariant }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(recordatorio.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("⏰ ${timeFormatter.format(fecha)}", fontSize = 14.sp, color = Color.White)
            }
            recordatorio.nota?.let {
                Spacer(Modifier.height(8.dp))
                Text("📝 $it", fontSize = 14.sp, color = Color.White)
            }
        }
    }
}


@Composable
fun FabMenu(
    fabExpanded: Boolean,
    onToggle: () -> Unit,
    navigateToFormTarea: () -> Unit,
    navigateToExamen: () -> Unit,
    navigateToRecordatorio: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        if (fabExpanded) Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.BottomEnd) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 70.dp)) {
                if (fabExpanded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nueva Tarea", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        FloatingActionButton(onClick = { navigateToFormTarea(); onToggle() }) { Icon(Icons.Default.Edit, contentDescription = "Nueva Tarea") }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nuevo Examen", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        FloatingActionButton(onClick = { navigateToExamen(); onToggle() }) { Icon(Icons.Default.AccountBox, contentDescription = "Nuevo Examen") }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nuevo Recordatorio", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        FloatingActionButton(onClick = { navigateToRecordatorio(); onToggle() }) { Icon(Icons.Default.Notifications, contentDescription = "Nuevo Recordatorio") }
                    }
                }
            }
            FloatingActionButton(onClick = { onToggle() }) {
                Icon(imageVector = if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (fabExpanded) "Cerrar" else "Agregar")
            }
        }
    }
}

// --- AGENDA SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    navigateToInicio: () -> Unit = {},
    navigateToCalendario: () -> Unit = {},
    navigateToHorarioDiario: () -> Unit = {},
    navigateToCuentas: () -> Unit = {},
    navigateTotarea: () -> Unit = {},
    navigateToAjustes: () -> Unit = {},
    navigateToSalir: () -> Unit = {},
    navigateToFormTarea: () -> Unit = {},
    navigatetoAsignatura: () -> Unit= {},
    navigateToExamen: () -> Unit = {},
    navigateToRecordatorio: () -> Unit = {},
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(4) }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home, navigateToInicio),
        NavItem("Calendario", Icons.Default.DateRange, navigateToCalendario),
        NavItem("Horario Diario", Icons.Default.List, navigateToHorarioDiario),
        NavItem("Ahorros", Icons.Default.Face, navigateToCuentas),
        NavItem("Agenda", Icons.Default.AccountBox, navigateTotarea)
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

    val asignaturas = remember { mutableStateListOf<com.example.appfirst.data.local.entity.Asignatura>() }

    LaunchedEffect(Unit) {
        val userEmail = UserPrefs.getLoggedUserEmail(context)
        val userId = AppDatabase.get(context).userDao()
            .getAllUsers().first().firstOrNull { it.email == userEmail }?.id

        if (userId != null) {
            AppDatabase.get(context).asignaturaDao().getAsignaturasByUser(userId).collect { listaEntity ->
                asignaturas.clear()
                asignaturas.addAll(listaEntity)
            }
        }
    }



    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Text("Menú", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
            navItems.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    label = { Text(item.label) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index; scope.launch { drawerState.close() }; item.onClick() },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            Divider(Modifier.padding(vertical = 8.dp))
            drawerExtraItems.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.label) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; item.onClick() },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Agenda") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavDestination.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                when (destination) {
                                    NavDestination.HOME -> navigateToInicio()
                                    NavDestination.CALENDAR -> navigateToCalendario()
                                    NavDestination.SCHEDULE -> navigateToHorarioDiario()
                                    NavDestination.SAVINGS -> navigateToCuentas()
                                    NavDestination.TASKS -> navigateTotarea()
                                }
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            },
            floatingActionButton = {
                FabMenu(
                    fabExpanded,
                    { fabExpanded = !fabExpanded },
                    navigateToFormTarea,
                    navigateToExamen,
                    navigateToRecordatorio
                )
            }
        ) { innerPadding ->
            if (isLoading) Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            else Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(onClick = { navigateToCalendario() }) {
                        Text(
                            "Horario",
                            fontSize = 12.sp
                        )
                    }
                    ElevatedButton(onClick = { }) { Text("Agenda", fontSize = 12.sp) }
                    ElevatedButton(onClick = { navigateToCalendario() }) {
                        Text(
                            "Calendario",
                            fontSize = 12.sp
                        )
                    }
                    ElevatedButton(onClick = { navigatetoAsignatura() }) {
                        Text(
                            "Asignatura",
                            fontSize = 12.sp
                        )
                    }
                }

                val dateFormat = remember { SimpleDateFormat("EEEE", Locale("es", "ES")) }
                val dateFormatFull = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }
                val dateFormatCompare =
                    remember { SimpleDateFormat("yyyyMMdd", Locale("es", "ES")) }
                val now = remember {
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0); set(
                        Calendar.MINUTE,
                        0
                    ); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(7) { i ->
                        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }
                        val titulo = when(i) {
                            0 -> "Hoy"
                            1 -> "Mañana"
                            else -> dateFormat.format(cal.time).replaceFirstChar { it.uppercase() }
                        }
                        val fecha = dateFormatFull.format(cal.time)
                        val fechaCompare = dateFormatCompare.format(cal.time)

                        val tareasDelDia = tareas.filter { dateFormatCompare.format(Date(it.fechaEntrega)) == fechaCompare }
                        val examenesDelDia = examenes.filter { dateFormatCompare.format(Date(it.fechaExamen)) == fechaCompare }
                        val recordatoriosDelDia = recordatorios.filter { dateFormatCompare.format(Date(it.fechaRecordatorio)) == fechaCompare }

                        val asignaturaMap = asignaturas.associateBy { it.id }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                            Text(fecha, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.DarkGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (tareasDelDia.isEmpty() && examenesDelDia.isEmpty() && recordatoriosDelDia.isEmpty()) {
                                    Text("No hay eventos", fontSize = 14.sp)
                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("Agregar evento", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        IconButton(onClick = { fabExpanded = true }) {
                                            Icon(Icons.Default.Add, contentDescription = "Agregar")
                                        }
                                    }

                                } else {
                                    tareasDelDia.forEach { tarea ->
                                        TareaItemSimpleAgenda(
                                            tarea = tarea,
                                            esPasada = tarea.fechaEntrega < now,
                                            asignaturaMap = asignaturaMap
                                        )
                                    }

                                    examenesDelDia.forEach { examen ->
                                        ExamenItemSimpleAgenda(
                                            examen = examen,
                                            esPasado = examen.fechaExamen < now,
                                            asignaturaMap = asignaturaMap
                                        )
                                    }

                                    recordatoriosDelDia.forEach { RecordatorioItemSimpleAgenda(it, it.fechaRecordatorio < now) }
                                }
                            }
                        }
                    }
                }


            }
        }
    }
    }