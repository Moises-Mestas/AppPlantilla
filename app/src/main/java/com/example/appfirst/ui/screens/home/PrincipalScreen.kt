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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import kotlinx.coroutines.launch

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
    navigateToCuentas:()-> Unit = {}
) {
    val viewModel = rememberIngresoVM()
    val montoTotal by viewModel.montoTotal.collectAsState()
    val montoTotalTarjeta by viewModel.montoTotalTarjeta.collectAsState()
    val montoTotalEfectivo by viewModel.montoTotalEfectivo.collectAsState()
    val montoTotalYape by viewModel.montoTotalYape.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                        Text(
                            text = "No tienes eventos hoy",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Disfruta de tu día libre",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar evento",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text("Agregar Evento")
                            }
                        }
                    }
                }


                // Tarjeta de gastos
// Tarjeta de gastos
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
                        // Título: Monedero: con el monto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "Monedero:",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Estructura de las columnas
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Start // Alineamos todo a la izquierda
                        ) {
                            Text("Cuentas", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("Total", fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("Ingreso", fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("Egreso", fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                        }

                        Divider(Modifier.padding(vertical = 8.dp))

                        // Datos de ejemplo (puedes ajustarlo a tu lógica de datos)
                        // Filas para "Tarjeta", "Efectivo", "Yape"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start // Alineamos todo a la izquierda
                        ) {
                            Text("Tarjeta", modifier = Modifier.weight(1f))
                            Text("S/ ${"%.2f".format(montoTotalTarjeta)}", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("S/ ${"%.2f".format(montoTotalTarjeta)}", color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("S/ 0.00", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start // Alineamos todo a la izquierda
                        ) {
                            Text("Efectivo", modifier = Modifier.weight(1f))
                            Text("S/ ${"%.2f".format(montoTotalEfectivo)}", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("S/ ${"%.2f".format(montoTotalEfectivo)}", color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("S/ 0.00", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start // Alineamos todo a la izquierda
                        ) {
                            Text("Yape", modifier = Modifier.weight(1f))
                            Text("S/ ${"%.2f".format(montoTotalYape)}", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("S/ ${"%.2f".format(montoTotalYape)}", color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                            Text("S/ 0.00", textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
                        }

                        Divider(Modifier.padding(vertical = 8.dp))

                        // Total de los ingresos y egresos (alineado con la fila anterior)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Start // Alineamos a la izquierda
                        ) {
                            Text("Total:", fontWeight = FontWeight.Bold)

                            // Agregar Spacer para controlar la distancia
                            Spacer(modifier = Modifier.width(50.dp)) // Ajusta este valor a la distancia que deseas

                            Text(
                                text = "S/ ${"%.2f".format(montoTotal)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }


            }
        }
    }
}
