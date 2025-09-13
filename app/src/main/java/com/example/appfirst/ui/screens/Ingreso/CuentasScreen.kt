package com.example.appfirst.ui.screens.cuentas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appfirst.ui.screens.home.NavDestination
import com.example.appfirst.ui.screens.home.NavItem
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    modifier: Modifier = Modifier,
    navigateToInicio: () -> Unit = {},
    navigateToCalendario: () -> Unit = {},
    navigateToHorarioDiario: () -> Unit = {},
    navigateToCuentas: () -> Unit = {},
    navigateTotarea: () -> Unit = {},
    navigateToAjustes: () -> Unit = {},
    navigateToSalir: () -> Unit = {},
    navigateToHistorial: () -> Unit = {},
    navigateToIngreso: () -> Unit = {},
    navigateToGastos: () -> Unit = {},
    navigateToIngreso2: () -> Unit = {},
    navigateBack : () -> Unit = {},
    navController: NavHostController,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val viewModel = rememberIngresoVM()
    val montoTotal by viewModel.montoTotal.collectAsState()
    val montoTotalTarjeta by viewModel.montoTotalTarjeta.collectAsState()
    val montoTotalEfectivo by viewModel.montoTotalEfectivo.collectAsState()
    val montoTotalYape by viewModel.montoTotalYape.collectAsState()
    var open by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home, navigateToInicio),
        NavItem("Calendario", Icons.Default.DateRange, navigateToCalendario),
        NavItem("Horario Diario", Icons.Default.List, navigateToHorarioDiario),
        NavItem("Cuentas", Icons.Default.Face, navigateToCuentas),
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
                    title = { Text("Vista Cuentas", fontSize = 22.sp) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
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
                            icon = { Icon(destination.icon, contentDescription = destination.contentDescription) },
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
                // CABECERA
                Text(
                    text = "Cuentas",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = "Monto Total: S/ ${"%.2f".format(montoTotal)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )

                // Tarjetas de categorías
                CuentaCard("TARJETA", montoTotalTarjeta)
                CuentaCard("EFECTIVO", montoTotalEfectivo)
                CuentaCard("YAPE", montoTotalYape)
            }

            // FAB y extras
            AddFabWithSheet(
                sheetOffsetY = -30.dp,
                bottomPadding = innerPadding.calculateBottomPadding(),
                open = open,
                onOpenChange = { open = it },
                navigateToGastos = navigateToGastos,
                navigateToHistorial = navigateToHistorial,
                navigateToIngreso2 = navigateToIngreso2,
                navigateToIngreso = navigateToIngreso
            )

            if (!open) {
                HistorialButton(navigateToHistorial = navigateToHistorial)
            }
        }
    }
}

@Composable
fun CuentaCard(title: String, monto: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("S/ ${"%.2f".format(monto)}", fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AddFabWithSheet(
    sheetOffsetY: Dp = 80.dp,
    bottomPadding: Dp = 0.dp,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    navigateToGastos: () -> Unit,
    navigateToIngreso2: () -> Unit,
    navigateToHistorial: () -> Unit,
    navigateToIngreso: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { onOpenChange(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 50.dp + bottomPadding),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
        }

        if (open) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                    .clickable { onOpenChange(false) }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp + bottomPadding)
                    .offset(y = sheetOffsetY),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SheetButton("Gasto", "Registra una compra o pago", Icons.Outlined.ShoppingCart) {
                    navigateToGastos()
                    onOpenChange(false)
                }
                SheetButton("Ingreso", "Registra un salario o ingreso", Icons.Filled.AttachMoney) {
                    navigateToIngreso2()
                    onOpenChange(false)
                }
            }
        }
    }
}

@Composable
fun SheetButton(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RectangleShape,
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(34.dp))
                Spacer(Modifier.width(12.dp))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HistorialButton(navigateToHistorial: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = navigateToHistorial,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 155.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.History, contentDescription = "Historial", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
