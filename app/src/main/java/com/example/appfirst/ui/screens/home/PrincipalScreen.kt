package com.example.appfirst.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.ingresos.FechaSeleccionadaSection1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// Agregar los imports necesarios para la funcionalidad de agenda
import com.example.appfirst.ui.tarea.rememberTareaVM
import com.example.appfirst.ui.examen.rememberExamenVM
import com.example.appfirst.ui.recordatorio.rememberRecordatorioVM
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.data.local.entity.Examen
import com.example.appfirst.data.local.entity.Recordatorio

data class NavItem(val label: String, val icon: ImageVector, val onClick: () -> Unit)

enum class NavDestination(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    HOME(Icons.Default.Home, "Inicio", "Icono de inicio"),
    CALENDAR(Icons.Default.DateRange, "Calendario", "Icono de calendario"),
    SCHEDULE(Icons.Default.List, "Horario", "Icono de horario"),
    SAVINGS(Icons.Default.Face, "Ahorros", "Icono de ahorros"),
    TASKS(Icons.Default.AccountBox, "Agenda", "Icono de agenda")
}

// Composable para mostrar tareas simples (similar al de Agenda)
@Composable
fun TareaItemSimpleHome(tarea: Tarea, esPasada: Boolean) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(tarea.fechaEntrega)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                tarea.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (esPasada) Color.Gray else MaterialTheme.colorScheme.primary
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏰ ${timeFormatter.format(fecha)}", fontSize = 12.sp)
                if (tarea.completada) {
                    Text("✓ Completada", fontSize = 12.sp, color = Color.Green)
                }
            }
        }
    }
}
fun formatAmount(amount: Double): String {
    // Limita el número a 6 dígitos enteros y un decimal
    val formatted = "%.1f".format(amount)

    // Verifica si el número excede los 6 dígitos enteros
    val parts = formatted.split(".")
    return if (parts[0].length > 6) {
        "${parts[0].take(5)}...${parts.getOrElse(1) { "" }}"  // Si excede, muestra los primeros 6 dígitos y agrega "..." (usamos getOrElse en caso de no tener decimales)
    } else {
        formatted
    }
}


// Composable para mostrar exámenes simples
@Composable
fun ExamenItemSimpleHome(examen: Examen, esPasado: Boolean) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fecha = Date(examen.fechaExamen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                examen.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (esPasado) Color.Gray else MaterialTheme.colorScheme.primary
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏰ ${timeFormatter.format(fecha)}", fontSize = 12.sp)
                examen.nota?.takeIf { it.isNotBlank() }?.let {
                    Text("Nota: $it", fontSize = 12.sp)
                }
            }
        }
    }
}

// Composable para mostrar recordatorios simples
@Composable
fun RecordatorioItemSimpleHome(recordatorio: Recordatorio, esPasado: Boolean) {
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                recordatorio.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text("⏰ ${timeFormatter.format(fecha)}", fontSize = 12.sp, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    modifier: Modifier = Modifier,
    navigateToInicio: () -> Unit = {},
    navigateToCalendario: () -> Unit = {},
    navigateToHorarioDiario: () -> Unit = {},
    navigateToAhorros: () -> Unit = {},
    navigateTotarea: () -> Unit = {},
    navigateToAjustes: () -> Unit = {},
    navigateToSalir: () -> Unit = {},
    navigateToCuentas: () -> Unit = {},
    navigateToFormTarea: () -> Unit = {},
    navigateToExamen: () -> Unit = {},
    navigateToRecordatorio: () -> Unit = {}
) {
    val viewModel = rememberIngresoVM()
    val fechaInicio by viewModel.fechaInicio.collectAsState()
    val fechaFin by viewModel.fechaFin.collectAsState()
    val context = LocalContext.current
    val userId = viewModel.userId

    val montoTotal by viewModel.montoTotal.collectAsState()
    val montoTotalTarjeta by viewModel.montoTotalTarjeta.collectAsState()
    val montoTotalEfectivo by viewModel.montoTotalEfectivo.collectAsState()
    val montoTotalYape by viewModel.montoTotalYape.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val ingresosTarjeta by viewModel.ingresosTarjeta.collectAsState()
    val gastosTarjeta by viewModel.gastosTarjeta.collectAsState()
    val ingresosEfectivo by viewModel.ingresosEfectivo.collectAsState()
    val gastosEfectivo by viewModel.gastosEfectivo.collectAsState()
    val ingresosYape by viewModel.ingresosYape.collectAsState()
    val gastosYape by viewModel.gastosYape.collectAsState()

    // ViewModels para agenda
    val tareaVM = rememberTareaVM()
    val tareas by tareaVM.tareas.collectAsState()
    val examenVM = rememberExamenVM()
    val examenes by examenVM.examenes.collectAsState()
    val recordatorioVM = rememberRecordatorioVM()
    val recordatorios by recordatorioVM.recordatorios.collectAsState()

    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

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

    // Cargar datos del usuario y eventos de agenda
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
                // Cargar datos de agenda
                tareaVM.setUserId(userId)
                examenVM.setUserId(userId)
                recordatorioVM.setUserId(userId)
            } else {
                errorMessage = "Usuario no encontrado"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Filtrar eventos para hoy
    val dateFormatCompare = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }
    val now = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val fechaHoy = dateFormatCompare.format(Date())
    val tareasHoy = tareas.filter { dateFormatCompare.format(Date(it.fechaEntrega)) == fechaHoy }
    val examenesHoy = examenes.filter { dateFormatCompare.format(Date(it.fechaExamen)) == fechaHoy }
    val recordatoriosHoy = recordatorios.filter { dateFormatCompare.format(Date(it.fechaRecordatorio)) == fechaHoy }

    val totalEventosHoy = tareasHoy.size + examenesHoy.size + recordatoriosHoy.size

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
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
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
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Bienvenido Usuario", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
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
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Tarjeta de eventos para hoy con funcionalidad de agenda
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Eventos para hoy",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (totalEventosHoy == 0) {
                            Text(
                                text = "No tienes eventos hoy",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        } else {
                            Text(
                                text = "Tienes $totalEventosHoy evento(s) para hoy",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Mostrar tareas de hoy
                            tareasHoy.forEach { tarea ->
                                TareaItemSimpleHome(
                                    tarea = tarea,
                                    esPasada = tarea.fechaEntrega < now
                                )
                            }

                            // Mostrar exámenes de hoy
                            examenesHoy.forEach { examen ->
                                ExamenItemSimpleHome(
                                    examen = examen,
                                    esPasado = examen.fechaExamen < now
                                )
                            }

                            // Mostrar recordatorios de hoy
                            recordatoriosHoy.forEach { recordatorio ->
                                RecordatorioItemSimpleHome(
                                    recordatorio = recordatorio,
                                    esPasado = recordatorio.fechaRecordatorio < now
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (totalEventosHoy == 0) "Disfruta de tu día libre" else "Gestiona tus eventos",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { navigateTotarea() }, // Navegar a la agenda
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar evento",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("Ver Agenda")
                            }
                        }
                    }
                }

                // El resto del código se mantiene igual...
                // Tarjeta de gastos (código original)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // ... (el resto del código de la tarjeta de gastos se mantiene igual)
                        // Título: Monedero con el monto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Monedero:",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 16.dp)
                            )

                            val totalIngresos = ingresosTarjeta + ingresosEfectivo + ingresosYape
                            val totalGastos = gastosTarjeta + gastosEfectivo + gastosYape
                            val totalFinal = totalIngresos - totalGastos
                            val percentage = if (totalFinal != 0.0) (totalGastos / totalFinal) * 100 else 0.0

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                modifier = Modifier.padding(start = 24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SentimentVerySatisfied,
                                    contentDescription = "Cara Alegre",
                                    tint = if (percentage in -50f..0f) Color(0xFF4CAF50) else Color(0xFF4CAF50),
                                    modifier = Modifier.size(if (percentage in -50f..0f) 48.dp else 32.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.SentimentNeutral,
                                    contentDescription = "Cara Seria",
                                    tint = if (percentage in -80f..-51f) Color(0xFFFFA000) else Color(0xFFFFA000),
                                    modifier = Modifier.size(if (percentage in -80f..-51f) 48.dp else 32.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.SentimentVeryDissatisfied,
                                    contentDescription = "Cara Triste",
                                    tint = if (percentage <= -81.0) Color(0xFFFF0000) else Color(0xFFFF0000),
                                    modifier = Modifier.size(if (percentage <= -81.0) 48.dp else 32.dp)
                                )
                            }
                        }

                        Divider(Modifier.padding(vertical = 8.dp))

                        // Estructura de las columnas
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                "Cuentas",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Ingreso",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Gasto",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Total",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Divider(Modifier.padding(vertical = 8.dp))

                        // Filas para "Tarjeta", "Efectivo", "Yape"
                        // Tarjeta
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text("Tarjeta", modifier = Modifier.weight(1f))
                            Text(
                                "S/ ${formatAmount(ingresosTarjeta - gastosTarjeta)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                "S/ ${formatAmount(gastosTarjeta)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFFF0000)
                            )
                            Text(
                                "S/ ${formatAmount(ingresosTarjeta)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight(900)
                            )
                        }

// Efectivo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text("Efectivo", modifier = Modifier.weight(1f))
                            Text(
                                "S/ ${formatAmount(ingresosEfectivo - gastosEfectivo)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                "S/ ${formatAmount(gastosEfectivo)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFFF0000)
                            )
                            Text(
                                "S/ ${formatAmount(ingresosEfectivo)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight(900)
                            )
                        }

// Yape
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text("Yape", modifier = Modifier.weight(1f))
                            Text(
                                "S/ ${formatAmount(ingresosYape - gastosYape)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                "S/ ${formatAmount(gastosYape)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFFF0000)
                            )
                            Text(
                                "S/ ${formatAmount(ingresosYape)}",  // Usamos la función aquí
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight(900)
                            )
                        }

// Total de los ingresos y egresos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text("TOTAL:", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(
                                text = "S/ ${formatAmount(ingresosTarjeta + ingresosEfectivo + ingresosYape - gastosTarjeta - gastosEfectivo - gastosYape)}",  // Usamos la función aquí
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "S/ ${formatAmount(gastosTarjeta + gastosEfectivo + gastosYape)}",  // Usamos la función aquí
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF0000),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                            )

                            Text(
                                text = "S/ ${formatAmount(ingresosTarjeta + ingresosEfectivo + ingresosYape)}",  // Usamos la función aquí
                                fontWeight = FontWeight(900),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f),
                            )
                        }


                        Divider(Modifier.padding(vertical = 4.dp))


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(
                                "Filtrar por fechas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            FechaSeleccionadaSection1(
                                fecha = fechaInicio ?: System.currentTimeMillis(),
                                onFechaChange = { nuevaFecha ->
                                    viewModel.updateFechaInicio(nuevaFecha)
                                }
                            )

                            FechaSeleccionadaSection1(
                                fecha = fechaFin ?: System.currentTimeMillis(),
                                onFechaChange = { nuevaFecha ->
                                    viewModel.updateFechaFin(nuevaFecha)
                                }
                            )

                            Divider(Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val userId = viewModel.userId
                                        if (userId != null) {
                                            viewModel.viewModelScope.launch {
                                                viewModel.updateIngresosYGastosPorFechas(
                                                    userId,
                                                    fechaInicio,
                                                    fechaFin
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                ) {
                                    Text("Aplicar filtro", fontSize = 14.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.viewModelScope.launch {
                                            viewModel.resetFilters()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                ) {
                                    Text("Restablecer", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}