package com.example.appfirst.ui.ingresos

// imports
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.appfirst.ui.screens.home.NavDestination
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.ingreso.IngresoViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.ui.screens.ingreso.AddFabWithSheet
import com.example.appfirst.ui.screens.ingreso.HistorialButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoScreen(
    navController: NavController, // Recibe el navController
    gastoId: Int? = null,                // <<< NUEVO

    navigateToCuentas: () -> Unit,
    navigateToHistorial: () -> Unit,

    navigateToIngreso2: () -> Unit,
    navigateBack: () -> Unit
) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current
    var open by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        try {
            val userDao = AppDatabase.get(context).userDao()
            val userId = withContext(Dispatchers.IO) {
                val email = UserPrefs.getLoggedUserEmail(context)
                val users = userDao.getAllUsers().first()
                users.firstOrNull { it.email == email }?.id
            }
            if (userId != null) {
                viewModel.setUserId(userId)
                if (gastoId != null) viewModel.loadForEdit(gastoId) else viewModel.startCreate()
            }
        } catch (_: Exception) {}
    }


    LaunchedEffect(Unit) {
        try {
            val userDao = AppDatabase.get(context).userDao()
            val userId = withContext(Dispatchers.IO) {
                val email = UserPrefs.getLoggedUserEmail(context)
                val users = userDao.getAllUsers().first()
                users.firstOrNull { it.email == email }?.id
            }
            if (userId != null) viewModel.setUserId(userId)
        } catch (_: Exception) { }
    }

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text("--- GASTO ---", fontWeight = FontWeight.Bold)
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
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GastoFormScreen(
                viewModel = viewModel,
                onSuccess = { navigateToCuentas() },  // Cambiar navigateBack() a navigateToCuentas()
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
            )

            AddFabWithSheet2(
                sheetOffsetY = 90.dp,
                bottomPadding = innerPadding.calculateBottomPadding(),
                open = open,
                onOpenChange = { open = it },
                navigateToGastos = navigateToCuentas,
                navigateToHistorial = navigateToHistorial, // Pasamos la función de navegación
                navigateToIngreso = navigateToIngreso2  // Aquí aseguramos que la función correcta es pasada
            )
            if (!open) {  // Mostrar HistorialButton solo si el popup está cerrado
                HistorialButton2(navigateToHistorial = { navController.navigate("historial") })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoFormScreen(
    viewModel: IngresoViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val form by viewModel.form.collectAsState()
    val message by viewModel.message.collectAsState()
    val navigateToSuccess by viewModel.navigateToSuccess.collectAsState()

    LaunchedEffect(Unit) {
        if (form.fecha.isBlank()) {
            viewModel.onFormChange(fecha = System.currentTimeMillis().toString())
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateMillis = form.fecha.toLongOrNull() ?: System.currentTimeMillis()

    LaunchedEffect(navigateToSuccess) {
        if (navigateToSuccess != null) {
            onSuccess()
            viewModel.resetNavigation()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Agregar Gasto",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))

        Text("Monto", fontSize = 22.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.monto,
            onValueChange = { new -> viewModel.onFormChange(monto = new.replace(',', '.')) },
            placeholder = { Text("Ingrese monto") },
            isError = form.errors.containsKey("monto"),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        form.errors["monto"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(20.dp))

        Text("Descripción:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = form.descripcion,
            onValueChange = { viewModel.onFormChange(descripcion = it) },
            label = { Text("Descripción") },
            isError = form.errors.containsKey("descripcion"),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Text("Depositado en:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            val labelActual = when (form.depositadoEn) {
                com.example.appfirst.data.local.entity.MedioPago.TARJETA -> "TARJETA"
                com.example.appfirst.data.local.entity.MedioPago.YAPE -> "YAPE"
                com.example.appfirst.data.local.entity.MedioPago.EFECTIVO -> "EFECTIVO"
            }

            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = labelActual,
                onValueChange = {},
                isError = form.errors.containsKey("depositadoEn"),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                placeholder = { Text("Selecciona un medio") }
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                com.example.appfirst.data.local.entity.MedioPago.values().forEach { option ->
                    val label = when (option) {
                        com.example.appfirst.data.local.entity.MedioPago.TARJETA -> "Tarjeta"
                        com.example.appfirst.data.local.entity.MedioPago.YAPE -> "Yape"
                        com.example.appfirst.data.local.entity.MedioPago.EFECTIVO -> "Efectivo"
                    }
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.onFormChange(depositadoEn = option)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Fecha Realizada:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Fecha: ${formatFecha(dateMillis)}", modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showDatePicker = true }) { Text("Cambiar fecha") }
        }

        Spacer(Modifier.height(20.dp))

        Text("Notas:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = form.notas,
            onValueChange = { viewModel.onFormChange(notas = it) },
            label = { Text("Notas (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            viewModel.save(isGasto = true)  // Pasamos isGasto = true para Gasto
        }, modifier = Modifier
            .fillMaxWidth(0.6f)  // Ajusta el ancho, aquí 80% del ancho máximo
        ) {
            Text(        "Guardar Gasto",
                fontSize = 20.sp,  // Aumenté el tamaño de la fuente
                fontWeight = FontWeight.Bold)
        }

        message?.let {
            Text(it, color = MaterialTheme.colorScheme.tertiary)
            LaunchedEffect(it) {
                delay(1500)
                viewModel.clearMessage()
            }
        }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            initialDate = Calendar.getInstance().apply { timeInMillis = dateMillis },
            onDateSelected = { year, month, day ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                viewModel.onFormChange(fecha = cal.timeInMillis.toString())
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun AddFabWithSheet2(
    sheetOffsetY: Dp = 80.dp,   // Ajusta la altura del sheet: +baja, -sube
    bottomPadding: Dp = 0.dp,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    navigateToGastos: () -> Unit,  // Función de navegación a GastoScreen
    navigateToHistorial: () -> Unit,

    navigateToIngreso: () -> Unit // Función de navegación a IngresoScreen2
) {
    Box(Modifier.fillMaxSize()) {
        // FAB (botón +)
        FloatingActionButton(
            onClick = { onOpenChange(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = -70.dp + bottomPadding), // Ajuste del FAB
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
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
fun HistorialButton2(
    navigateToHistorial: () -> Unit  // Función para navegar al HistorialScreen
) {
    Box(Modifier.fillMaxSize()) {  // Colocamos el FloatingActionButton dentro de un Box
        FloatingActionButton(
            onClick = { navigateToHistorial() }, // Acción de navegación al Historial
            modifier = Modifier
                .align(Alignment.BottomStart)  // Alineación en la parte inferior izquierda
                .padding(start = 16.dp, bottom = 30.dp), // Ajuste de padding para posicionarlo
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
