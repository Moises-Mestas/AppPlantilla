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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.ui.screens.home.NavItem
import com.example.appfirst.ui.tarea.rememberTareaVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

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
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Text(
                        text = "Finalizado",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            tarea.nota?.takeIf { it.isNotBlank() }?.let { nota ->
                Text(
                    text = nota,
                    fontSize = 12.sp,
                    color = if (esPasada) Color.Gray else MaterialTheme.colorScheme.secondary,
                    textDecoration = if (esPasada) TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }
    }
}

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
    navigatetoAsignatura: () -> Unit= {}
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

    val viewModel = rememberTareaVM()
    val tareas by viewModel.tareas.collectAsState()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // FAB Speed Dial state
    var fabExpanded by remember { mutableStateOf(false) }

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
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
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Nueva tarea")
                            }
                            FloatingActionButton(
                                onClick = { /* Acción 2 */ fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Acción 2")
                            }
                            FloatingActionButton(
                                onClick = { /* Acción 3 */ fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Acción 3")
                            }
                            FloatingActionButton(
                                onClick = { navigateToFormTarea(); fabExpanded = false },
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Nueva tarea")
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = {  },
                        modifier = Modifier.widthIn(min = 110.dp)
                    ) { Text("Horario", fontSize = 12.sp, maxLines = 1) }
                    ElevatedButton(
                        onClick = { },
                        modifier = Modifier.widthIn(min = 110.dp)
                    ) { Text("Agenda", fontSize = 12.sp, maxLines = 1) }
                    ElevatedButton(
                        onClick = { },
                        modifier = Modifier.widthIn(min = 110.dp)
                    ) { Text("Calendario", fontSize = 12.sp, maxLines = 1) }
                    ElevatedButton(
                        onClick = {  navigatetoAsignatura()},
                        modifier = Modifier.widthIn(min = 110.dp)
                    ) { Text("Asignatura", fontSize = 12.sp, maxLines = 1) }
                }

                // --- SCROLL VERTICAL PARA LOS CARDS DE LA SEMANA ---
                val dateFormat = remember { SimpleDateFormat("EEEE", Locale("es", "ES")) }
                val dateFormatFull = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")) }
                val dateFormatCompare =
                    remember { SimpleDateFormat("yyyyMMdd", Locale("es", "ES")) }
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

                        val tareasDelDia = tareas.filter { tarea ->
                            try {
                                val tareaFecha =
                                    dateFormatCompare.format(Date(tarea.fechaEntrega.toLong()))
                                tareaFecha == fechaCompare
                            } catch (e: Exception) {
                                false
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            // Título y fecha en la misma fila, bien separados
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
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
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Eventos Pendientes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (tareasDelDia.isEmpty()) {
                                    Text(
                                        text = "No hay eventos",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                } else {
                                    Column {
                                        tareasDelDia.forEach { tarea ->
                                            val esPasada = tarea.fechaEntrega < now
                                            TareaItemSimpleAgenda(tarea, esPasada)
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