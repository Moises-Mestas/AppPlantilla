package com.example.appfirst.ui.ingresos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date

import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.ingreso.IngresoViewModel
import com.example.appfirst.ui.screens.home.NavDestination


// 📌 Pantalla principal con AppBar + BottomNavigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoMainScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Datos de Ingreso",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Amigos") },
                    label = { Text("Amigos") }
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Ahorros") },
                    label = { Text("Ahorros") }
                )
                NavigationBarItem(
                    selected = selectedItem == 3,
                    onClick = { selectedItem = 3 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Chat") },
                    label = { Text("Chat") }
                )
            }
        }
    ) { innerPadding ->
        IngresoScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoScreen(
    modifier: Modifier = Modifier, // 🔹 agregado
    navigateBack: () -> Unit = {}
) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current
    val ingresos by viewModel.ingresos.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Obtener userId automáticamente
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
            } else {
                errorMessage = "Usuario no encontrado"
            }
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Mis Ingresos", fontWeight = FontWeight.Bold) },
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
            modifier = modifier // 🔹 usamos el modifier recibido
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (!showForm) {
                Button(
                    onClick = { showForm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Agregar ingreso")
                }
            }


            if (showForm) {
                IngresoFormScreen(
                    viewModel = viewModel,
                    onSuccess = { showForm = false }
                )
            } else {
                Spacer(Modifier.height(16.dp))
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error) }
                    }
                    ingresos.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text("No hay ingresos. ¡Agrega uno desde el formulario!") }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ingresos) { ingreso ->
                                IngresoItemSimple(ingreso = ingreso)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun IngresoItemSimple(ingreso: Ingreso) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = ingreso.descripcion,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Monto: S/ ${"%.2f".format(ingreso.monto)}",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Depositado en: ${ingreso.depositadoEn}",
                fontSize = 12.sp
            )
            if (ingreso.notas.isNotBlank()) {
                Text(
                    text = "Notas: ${ingreso.notas}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "Fecha: ${formatFecha(ingreso.fecha)}",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoFormScreen(
    viewModel: IngresoViewModel,
    onSuccess: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Título grande
        Text(
            text = "Agregar Ingreso",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(12.dp))

        // 🔹 Subtítulo "Monto"
        Text(
            text = "Monto",
            fontSize = 22.sp, // un poco más grande
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(8.dp)) // “un poquito” de espacio con el cuadro de abajo

        // Campo Monto
        OutlinedTextField(
            value = form.monto,
            onValueChange = { new -> viewModel.onFormChange(monto = new.replace(',', '.')) },
            placeholder = { Text("Ingrese monto") },
            isError = form.errors.containsKey("monto"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        form.errors["monto"]?.let { Text(it, color = Color.Red) }

        Spacer(Modifier.height(20.dp))
// Texto "Descripción:" alineado a la izquierda
        Text(
            text = "Descripción:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        // Campo Descripción
        OutlinedTextField(
            value = form.descripcion,
            onValueChange = { viewModel.onFormChange(descripcion = it) },
            label = { Text("Descripción") },
            isError = form.errors.containsKey("descripcion"),
            modifier = Modifier.fillMaxWidth()
        )
        form.errors["descripcion"]?.let { Text(it, color = Color.Red) }

        Spacer(Modifier.height(20.dp))


        // Texto "Descripción:" alineado a la izquierda
        Text(
            text = "Depositado en:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        // Campo Depositado en
        OutlinedTextField(
            value = form.depositadoEn,
            onValueChange = { viewModel.onFormChange(depositadoEn = it) },
            label = { Text("Depositado en") },
            isError = form.errors.containsKey("depositadoEn"),
            modifier = Modifier.fillMaxWidth()
        )
        form.errors["depositadoEn"]?.let { Text(it, color = Color.Red) }

        Spacer(Modifier.height(20.dp))


        // Texto "Descripción:" alineado a la izquierda
        Text(
            text = "Fecha Realizada:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        // Fecha
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Fecha: ${formatFecha(dateMillis)}", modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showDatePicker = true }) { Text("Cambiar fecha") }
        }
        form.errors["fecha"]?.let { Text(it, color = Color.Red) }

        Spacer(Modifier.height(20.dp))


        // Texto "Descripción:" alineado a la izquierda
        Text(
            text = "Notas:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        // Notas
        OutlinedTextField(
            value = form.notas,
            onValueChange = { viewModel.onFormChange(notas = it) },
            label = { Text("Notas (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar
        Button(
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        message?.let {
            Text(it, color = Color.Green)
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
private fun AppDatePickerDialog(
    initialDate: Calendar,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.timeInMillis
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                    onDateSelected(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun formatFecha(timestamp: Long): String {
    return try {
        android.text.format.DateFormat.format("dd/MM/yy HH:mm", Date(timestamp)).toString()
    } catch (e: Exception) {
        "Fecha inválida"
    }
}
