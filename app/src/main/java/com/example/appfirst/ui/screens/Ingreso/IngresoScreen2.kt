package com.example.appfirst.ui.ingresos


import com.example.appfirst.ui.screens.home.NavDestination
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.ingreso.IngresoViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase

// Opcionales si tu versión de Material3 los requiere explícitos:
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.appfirst.data.local.entity.TipoNota


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoScreen2(

    ingresoId: Int? = null,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigateToCalendario: () -> Unit = {},
    navigateToHorarioDiario: () -> Unit = {},
    navigateToCuentas: () -> Unit = {},
    navigateTotarea: () -> Unit = {},
    navigateToHistorial: () -> Unit = {},
    navigateToGastos: () -> Unit = {},
    navigateToIngreso2: () -> Unit = {},
    navigateToInicio: () -> Unit = { navController.navigate("principal") },
    navigateBack: () -> Unit = {}

) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current
    var open by remember { mutableStateOf(false) }
    val navigateToCuentasEvent by viewModel.navigateToCuentasEvent.collectAsState()
    LaunchedEffect(navigateToCuentasEvent) {
        if (navigateToCuentasEvent) {
            navigateToCuentas()
            // Después de navegar, reseteamos el evento para evitar navegación repetida
            viewModel.resetNavigation()
        }
    }
    val ingresoTypes = listOf(
        TipoNota.TRABAJO,
        TipoNota.BONOS,
        TipoNota.PROPINAS,
        TipoNota.INVERSIONES,
        TipoNota.OTROS
    )

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
                if (ingresoId != null) {
                    viewModel.loadForEdit(ingresoId)
                } else {
                    viewModel.startCreate()
                }
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
        } catch (_: Exception) {}
    }

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // Fondo blanco
                    titleContentColor = Color.Black, // Título negro
                ),
                title = {
                    Text("+++ Ingreso +++", fontWeight = FontWeight.Bold, fontSize = 25.sp)
                },
                navigationIcon = {
                    IconButton(onClick = navigateToCuentas) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black // Flecha negra
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
                            when (destination) {
                                NavDestination.HOME -> navigateToInicio()
                                NavDestination.CALENDAR -> navigateToCalendario()
                                NavDestination.SCHEDULE -> navigateToHorarioDiario()
                                NavDestination.SAVINGS -> navigateToCuentas()
                                NavDestination.TASKS -> navigateTotarea()
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.contentDescription) },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 10.6.sp, // Ajusta el tamaño de la fuente
                                fontWeight = FontWeight.Bold,
                                maxLines = 1, // Asegura que el texto solo ocupe una línea
                                overflow = TextOverflow.Ellipsis // Recorta el texto con "..." si es demasiado largo
                            ) }
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
            // Contenido desplazable
            Column(
                modifier = Modifier
                    .fillMaxWidth()  // Ocupa todo el ancho disponible
                    .padding(30.dp)  // Agregar padding consistente
                    .verticalScroll(rememberScrollState())  // Hace que el contenido sea desplazable
            ) {
                IngresoFormScreen(
                    viewModel = viewModel,
                    onSuccess = { navigateToCuentas() },
                    modifier = Modifier
                        .fillMaxWidth()  // Asegura que el formulario ocupe todo el ancho disponible
                )
            }

            // Botones superpuestos (Historial y Agregar)
            if (!open) {  // Mostrar botones solo si el popup está cerrado
                Box(Modifier.fillMaxSize()) {
                    FloatingActionButton(
                        onClick = { navigateToHistorial() },
                        modifier = Modifier
                            .align(Alignment.BottomStart) // Alinea a la izquierda
                            .padding(start = 16.dp, bottom = 30.dp), // Ajustamos la altura
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Historial",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    FloatingActionButton(
                        onClick = { open = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Alinea a la derecha
                            .padding(end = 16.dp, bottom = 30.dp), // Ajustamos la altura
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Agregar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Agregar el menú emergente para seleccionar "Ingreso" o "Gasto"
            if (open) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                        .clickable { open = false }  // Cerrar el sheet al hacer clic fuera
                )

                // Colocamos la Column dentro de un Box para usar align y ajustarlo
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp + innerPadding.calculateBottomPadding())
                            .offset(y = 90.dp), // Desplazamos hacia arriba
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Botón de "Ingreso"
                        SheetButton("Ingreso", "Registra un salario o ingreso", Icons.Filled.AttachMoney) {
                            navigateToIngreso2()
                            open = false  // Cerrar el sheet luego de navegar
                        }

                        // Botón de "Gasto"
                        SheetButton("Gasto", "Registra una compra o pago", Icons.Outlined.ShoppingCart) {
                            navigateToGastos()
                            open = false  // Cerrar el sheet luego de navegar
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoFormScreen(
    viewModel: IngresoViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val form by viewModel.form.collectAsState()
    val message by viewModel.message.collectAsState()
    val navigateToSuccess by viewModel.navigateToSuccess.collectAsState()
    val ingresoTypes = listOf(
        TipoNota.TRABAJO,
        TipoNota.BONOS,
        TipoNota.PROPINAS,
        TipoNota.INVERSIONES,
        TipoNota.OTROS
    )
    LaunchedEffect(Unit) {
        if (form.fecha.isBlank()) {
            viewModel.onFormChange(fecha = System.currentTimeMillis().toString())
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateMillis = form.fecha.toLongOrNull() ?: System.currentTimeMillis()

    LaunchedEffect(navigateToSuccess) {
        if (navigateToSuccess != null) {
            onSuccess() // Navegar a la vista de cuentas
            viewModel.resetNavigation() // Resetear la navegación para la siguiente vez
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Agregar Ingreso",
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






        // Para las Notas (TipoNota):
        Text("Categoria:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        var expanded2 by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded2, onExpandedChange = { expanded2 = !expanded2 }) {
            val labelActual = form.notas.display() // Mostrar el nombre legible de TipoNota
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = labelActual,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded2) },
                placeholder = { Text("Selecciona una nota") }
            )

            ExposedDropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                ingresoTypes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.display()) },
                        onClick = {
                            viewModel.onFormChange(notas = option)
                            expanded2 = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            viewModel.save(isGasto = false)


        }, modifier = Modifier
            .fillMaxWidth(0.6f)
        ) {
            Text(        "Guardar",
                fontSize = 25.sp,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    initialDate: Calendar,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate.timeInMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    onDateSelected(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = datePickerState)
    }
}


