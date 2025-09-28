package com.example.appfirst.ui.ingresos




import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.appfirst.ui.screens.home.NavDestination
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.TipoNota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoScreen(
    gastoId: Int? = null,
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
    val navigateToCuentasEvent by viewModel.navigateToCuentasEvent.collectAsState()
    val context = LocalContext.current
    var open by remember { mutableStateOf(false) }

    val gastoTypes = listOf(
        TipoNota.ALOJAMIENTO,
        TipoNota.COMIDA,
        TipoNota.ENTRETENIMIENTO,
        TipoNota.FAMILIA,
        TipoNota.MASCOTAS,
        TipoNota.EDUCACION,
        TipoNota.OTROS
    )
    LaunchedEffect(navigateToCuentasEvent) {
        if (navigateToCuentasEvent) {
            navigateToCuentas()
            // Después de navegar, resetea el evento de navegación
            viewModel.resetNavigation()
        }
    }
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

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                ),
                title = {
                    Text("--- Gasto ---", fontWeight = FontWeight.Bold, fontSize = 25.sp)
                },
                navigationIcon = {
                    IconButton(onClick = navigateToCuentas) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
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
                                fontSize = 10.6.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Contenido desplazable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                GastoFormScreen(
                    viewModel = viewModel,
                    onSuccess = { navigateToCuentas() },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }


            if (!open) {
                Box(Modifier.fillMaxSize()) {
                    FloatingActionButton(
                        onClick = { navigateToHistorial() },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 30.dp),
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
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 30.dp),
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

            // Agregar el menú emergente para seleccionar "Gasto" o "Ingreso"
            if (open) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                        .clickable { open = false }
                )

                // Colocamos la Column dentro de un Box para usar align y ajustarlo
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp + innerPadding.calculateBottomPadding())
                            .offset(y = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        SheetButton("Gasto", "Registra una compra o pago", Icons.Outlined.ShoppingCart) {
                            navigateToGastos()
                            open = false
                        }


                        SheetButton("Ingreso", "Registra un salario o ingreso", Icons.Filled.AttachMoney) {
                            navigateToIngreso2()
                            open = false
                        }
                    }
                }
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

    val gastoTypes = listOf(
        TipoNota.ALOJAMIENTO,
        TipoNota.COMIDA,
        TipoNota.ENTRETENIMIENTO,
        TipoNota.FAMILIA,
        TipoNota.MASCOTAS,
        TipoNota.EDUCACION,
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

        Text(
            "Monto",
            fontSize = 22.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
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

        Text(
            "Descripción:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = form.descripcion,
            onValueChange = { viewModel.onFormChange(descripcion = it) },
            label = { Text("Descripción") },
            isError = form.errors.containsKey("descripcion"),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Depositado en:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
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

        Text(
            "Fecha Realizada:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Fecha: ${formatFecha(dateMillis)}", modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showDatePicker = true }) { Text("Cambiar fecha") }
        }

        Spacer(Modifier.height(20.dp))

        // Notas (TipoNota) DropdownMenu
        Text(
            "Categoria:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        var expanded2 by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded2,
            onExpandedChange = { expanded2 = !expanded2 }) {
            val labelActual = form.notas.display()
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
                gastoTypes.forEach { option ->
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

        Button(
            onClick = {
                viewModel.save(isGasto = true)
            }, modifier = Modifier
                .fillMaxWidth(0.6f)
        ) {
            Text(
                "Guardar",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
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
fun AddFabWithSheet2(
    sheetOffsetY: Dp = 80.dp,
    bottomPadding: Dp = 0.dp,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    navigateToGastos: () -> Unit,
    navigateToHistorial: () -> Unit,
    navigateToIngreso: () -> Unit
) {

    Box(Modifier.fillMaxSize()) {
        // Eliminamos el botón "Agregar" visualmente
        if (open) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                    .clickable { onOpenChange(false) }  // Esto cierra el sheet al hacer clic afuera
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp + bottomPadding)
                    .offset(y = sheetOffsetY),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Botón de "Gasto"
                SheetButton("Gasto", "Registra una compra o pago", Icons.Outlined.ShoppingCart) {
                    navigateToGastos()
                    onOpenChange(false)  // Cierra el sheet luego de navegar
                }

                // Botón de "Ingreso"
                SheetButton("Ingreso", "Registra un salario o ingreso", Icons.Filled.AttachMoney) {
                    navigateToIngreso()
                    onOpenChange(false)  // Cierra el sheet luego de navegar
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
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp)) // Bordes redondeados
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) // Fondo semitransparente
            .shadow(8.dp, shape = RoundedCornerShape(16.dp), ambientColor = MaterialTheme.colorScheme.primary), // Sombra añadida aquí
        shape = RoundedCornerShape(16.dp), // Bordes redondeados
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary) // Aumentado tamaño del ícono
            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}