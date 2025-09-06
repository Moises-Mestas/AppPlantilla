// com.example.appfirst.ui.ingresos.HistorialScreen.kt
package com.example.appfirst.ui.ingresos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.screens.home.NavDestination
import com.example.appfirst.ui.screens.ingreso.AddFabWithSheet
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navigateToCuentas: () -> Unit,
    navigateToFormIngreso2: () -> Unit,  // Para navegar al formulario de ingreso
    navigateToFormGasto: () -> Unit,    // Para navegar al formulario de gasto

    navigateBack: () -> Unit,
    navigateToHistorial: () -> Unit  // Función para navegar al HistorialScreen
) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current
    val ingresos by viewModel.ingresos.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf(0) }
    var open by remember { mutableStateOf(false) } // Estado para la ventana emergente

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Carga userId
    LaunchedEffect(Unit) {
        try {
            val userDao = AppDatabase.get(context).userDao()
            val userId = withContext(Dispatchers.IO) {
                val userEmail = UserPrefs.getLoggedUserEmail(context)
                val users = userDao.getAllUsers().first()
                users.firstOrNull { it.email == userEmail }?.id
            }
            if (userId != null) viewModel.setUserId(userId) else errorMessage = "Usuario no encontrado"
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text("-+-+ HISTORIAL +-+- ", fontWeight = FontWeight.Bold, fontSize = 30.sp)
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
            NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                NavDestination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (index == 3) navigateToCuentas() // Navegar a cuentas
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
                .padding(16.dp)
        ) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
                ingresos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ingresos. ¡Agrega uno desde el botón!")
                }
                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingresos) { ingreso ->
                        IngresoItemSimple(ingreso = ingreso)
                    }
                }
            }
        }

        // Agregar botones FAB para acciones
        AddFabWithSheet3(
            sheetOffsetY = -20.dp,
            bottomPadding = innerPadding.calculateBottomPadding(),
            open = open, // Pasamos el estado de la ventana emergente
            onOpenChange = { open = it }, // Función para cambiar el estado de la ventana emergente
            navigateToGastos = navigateToFormGasto,
            navigateToHistorial = navigateToHistorial,
            navigateToIngreso = navigateToFormIngreso2
        )

        // Mostrar HistorialButton solo si el popup está cerrado
        if (!open) {
            HistorialButton(navigateToHistorial = navigateToHistorial)
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

@Composable
fun AddFabWithSheet3(
    sheetOffsetY: Dp = 80.dp,   // Ajusta la altura del sheet: +baja, -sube
    bottomPadding: Dp = 0.dp,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    navigateToGastos: () -> Unit,  // Función de navegación a GastoScreen
    navigateToHistorial: () -> Unit,  // Función de navegación a HistorialScreen
    navigateToIngreso: () -> Unit // Función de navegación a IngresoScreen2
) {
    Box(Modifier.fillMaxSize()) {

        // FAB (botón +)
        FloatingActionButton(
            onClick = { onOpenChange(true) }, // Abre el popup al presionar el FAB
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
                    .clickable() { onOpenChange(false) } // Cerrar al hacer clic en el fondo oscuro
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
fun IngresoItemSimple(ingreso: Ingreso) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ingreso.descripcion,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Monto: S/ ${"%.2f".format(ingreso.monto)}", fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            Text("Depositado en: ${ingreso.depositadoEn}", fontSize = 12.sp)
            if (ingreso.notas.isNotBlank()) {
                Text("Notas: ${ingreso.notas}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Text("Fecha: ${formatFecha(ingreso.fecha)}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// 👇 Deja este helper aquí para que lo use la lista (y también la vista 2 lo verá por estar en el mismo package)
fun formatFecha(timestamp: Long): String = try {
    android.text.format.DateFormat.format("dd/MM/yy HH:mm", Date(timestamp)).toString()
} catch (e: Exception) {
    "Fecha inválida"
}
