package com.example.appfirst.ui.ingresos

// imports
import com.example.appfirst.ui.screens.home.NavDestination
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
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
import androidx.compose.ui.graphics.RectangleShape
import com.example.appfirst.ui.screens.ingreso.AddFabWithSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoScreen2(
    navigateToCuentas: () -> Unit, // Agregamos la nueva función de navegación
    navigateToGastos: () -> Unit,

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
            if (userId != null) viewModel.setUserId(userId)
        } catch (_: Exception) {
        }
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
                    Text("+ Ingreso +", fontWeight = FontWeight.Bold)
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
                            // Navegar a la vista de Cuentas si el icono de "Ahorros" es presionado
                            if (index == 3) navigateToCuentas() // Índice de Ahorros
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
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            IngresoFormScreen(
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
                navigateToIngreso = navigateToCuentas, // Aquí puedes agregar la función de navegación para `IngresoScreen2`

                navigateToGastos = navigateToGastos // Función correcta de navegación
            )



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

        Text("Notas:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = form.notas,
            onValueChange = { viewModel.onFormChange(notas = it) },
            label = { Text("Notas (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

// En la pantalla IngresoScreen2, cuando se guarde, pasar isGasto = false
        Button(onClick = {
            viewModel.save(isGasto = false)  // Pasamos isGasto como false
        }, modifier = Modifier
            .fillMaxWidth(0.6f)  // Ajusta el ancho, aquí 80% del ancho máximo
        ) {
            Text(        "Guardar Ingreso",
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


