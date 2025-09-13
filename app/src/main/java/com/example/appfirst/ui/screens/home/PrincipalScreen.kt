package com.example.appfirst.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class NavItem(val label: String, val icon: ImageVector, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    modifier: Modifier = Modifier,
    navigateToInicio: () -> Unit = {},
    navigateToAhorros: () -> Unit = {},
    navigateTotarea: () -> Unit = {},
    navigateToCalendario: () -> Unit = {},
    navigateToHorarioDiario: () -> Unit = {},
    navigateToAmigos: () -> Unit = {},
    navigateToAjustes: () -> Unit = {},
    navigateToSalir: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableStateOf(0) }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home, navigateToInicio),
        NavItem("Ahorros", Icons.Default.Add, navigateToAhorros),
        NavItem("Notas", Icons.Default.AccountBox, navigateTotarea),
        NavItem("Calendario", Icons.Default.DateRange, navigateToCalendario),
        NavItem("Horario Diario", Icons.Default.List, navigateToHorarioDiario),
        NavItem("Amigos", Icons.Default.Face, navigateToAmigos)
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
                    title = { Text("Bienvenido Usuario") },
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
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pantalla principal",
                    fontSize = 22.sp
                )
            }
        }
    }
}