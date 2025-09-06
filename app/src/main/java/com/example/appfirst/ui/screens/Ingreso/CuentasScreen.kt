package com.example.appfirst.ui.screens.ingreso

import androidx.compose.material.icons.filled.History // Si no lo necesitas, elimínalo.
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.appfirst.ui.screens.home.NavDestination
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appfirst.ui.ingreso.rememberIngresoVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    navController: NavController, // Recibe el navController
    navigateToHistorial: () -> Unit,

    navigateBack: () -> Unit, // Función para navegar atrás

    navigateToIngreso2: () -> Unit,
    navigateToGastos: () -> Unit
) {
    val viewModel = rememberIngresoVM() // Asegúrate de obtener el viewModel
    val montoTotal by viewModel.montoTotal.collectAsState()  // Obtén el monto total
    val montoTotalTarjeta by viewModel.montoTotalTarjeta.collectAsState()
    val montoTotalEfectivo by viewModel.montoTotalEfectivo.collectAsState()
    val montoTotalYape by viewModel.montoTotalYape.collectAsState()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    var open by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text("Vista Cuentas", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary // Cambiar el color del icono a blanco
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                val navItems = listOf(
                    NavDestination.DATE,
                    NavDestination.SONGS,
                    NavDestination.HOME,
                    NavDestination.FAVORITES,
                    NavDestination.PROFILE
                )
                navItems.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
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
            // Título "Cuentas"
            Text(
                text = "Cuentas:",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp) // Ajuste de padding superior
            )
            // Mostrar el monto total
            Text(
                text = "Monto Total: S/ ${"%.2f".format(montoTotalTarjeta + montoTotalEfectivo + montoTotalYape)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            // Sección TARJETA (morada)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "TARJETA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Cuadro gris TARJETA (botón)
            Button(
                onClick = { /* Acción para tarjeta */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Detalles de tarjeta...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(0.7f) // Asegurando que el texto ocupe solo parte del botón
                    )
                    Text(
                        text = "S/ ${"%.2f".format(montoTotalTarjeta)}", // Monto total de tarjeta
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Sección EFECTIVO (morada)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "EFECTIVO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Cuadro gris EFECTIVO (botón)
            Button(
                onClick = { /* Acción efectivo */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles de efectivo...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                    Text(
                        text = "S/ ${"%.2f".format(montoTotalEfectivo)}", // Monto total de efectivo
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Sección YAPE (morada)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "YAPE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Cuadro gris YAPE (botón)
            Button(
                onClick = { /* Acción yape */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles de yape...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                    Text(
                        text = "S/ ${"%.2f".format(montoTotalYape)}", // Monto total de yape
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        AddFabWithSheet(
            sheetOffsetY = -30.dp,
            bottomPadding = innerPadding.calculateBottomPadding(),
            open = open,
            onOpenChange = { open = it },
            navigateToGastos = navigateToGastos, // Pasamos la función de navegación
            navigateToHistorial = navigateToHistorial, // Pasamos la función de navegación

            navigateToIngreso = navigateToIngreso2  // Pasamos la función de navegación
        )

        if (!open) {  // Mostrar HistorialButton solo si el popup está cerrado
            HistorialButton(navigateToHistorial = { navController.navigate("historial") })
        }

    }
}


@Composable
fun AddFabWithSheet(
    sheetOffsetY: Dp = 80.dp,   // Ajusta la altura del sheet: +baja, -sube
    bottomPadding: Dp = 0.dp,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    navigateToGastos: () -> Unit,  // Función de navegación a GastoScreen
    navigateToHistorial: () -> Unit,  // Función de navegación a GastoScreen

    navigateToIngreso: () -> Unit // Función de navegación a IngresoScreen2

) {
    Box(Modifier.fillMaxSize()) {

        // FAB (botón +)
        FloatingActionButton(
            onClick = { onOpenChange(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 50.dp + bottomPadding), // Ajuste del FAB
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Agregar",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }









        if (open) {
            // Fondo oscuro
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                    .clickable { onOpenChange(false) } // Cerrar al hacer clic en el fondo oscuro
            )

            // Solo los botones (sin fondo)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp + bottomPadding)
                    .offset(y = sheetOffsetY),
                verticalArrangement = Arrangement.spacedBy(20.dp) // espacio entre botones
            ) {
                // BOTÓN GASTO
                ElevatedButton(
                    onClick = {
                        navigateToGastos() // Navegar a GastoScreen
                        onOpenChange(false) // Cerrar la ventana emergente
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RectangleShape, // cuadrado
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp) // icono mediano
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Gasto", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Registra una compra o un pago/gasto que hiciste en tu día.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // BOTÓN INGRESO
                ElevatedButton(
                    onClick = {
                        navigateToIngreso() // Navegar a IngresoScreen2
                        onOpenChange(false) // Cerrar la ventana emergente
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Ingreso", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Registra tu salario, bonos o algún ingreso obtenido en tu día.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


    @Composable
    fun HistorialButton(
        navigateToHistorial: () -> Unit  // Función para navegar al HistorialScreen
    ) {
        Box(Modifier.fillMaxSize()) {  // Colocamos el FloatingActionButton dentro de un Box
            FloatingActionButton(
                onClick = { navigateToHistorial() }, // Acción de navegación al Historial
                modifier = Modifier
                    .align(Alignment.BottomStart)  // Alineación en la parte inferior izquierda
                    .padding(start = 16.dp, bottom = 155.dp), // Ajuste de padding para posicionarlo
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.History,  // Icono de historial
                    contentDescription = "Historial",
                    tint = MaterialTheme.colorScheme.onPrimary  // Ajustar color
                )
            }
        }
    }
