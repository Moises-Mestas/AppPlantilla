// com.example.appfirst.ui.ingresos.IngresoScreen2.kt
package com.example.appfirst.ui.ingresos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoScreen2(
    navigateBack: () -> Unit
) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current

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
            } else {
                // Si quieres, puedes navegar atrás o mostrar un Snackbar aquí
                // navigateBack()
            }
        } catch (e: Exception) {
            // opcional: log o mensaje de error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Ingreso", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        IngresoFormScreen(
            viewModel = viewModel,
            onSuccess = { navigateBack() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
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
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agregar Ingreso", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))

        Text("Monto", fontSize = 22.sp, modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
        form.errors["descripcion"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(20.dp))

        Text("Depositado en:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            val labelActual = when (form.depositadoEn) {
                com.example.appfirst.data.local.entity.MedioPago.TARJETA -> "TARJETA"
                com.example.appfirst.data.local.entity.MedioPago.YAPE -> "YAPE"
                com.example.appfirst.data.local.entity.MedioPago.EFECTIVO -> "EFECTIVO"
            }

            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor() // si tu versión no lo tiene, quita esta línea
                    .fillMaxWidth(),
                readOnly = true,
                value = labelActual,
                onValueChange = {},
                isError = form.errors.containsKey("depositadoEn"),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                placeholder = { Text("Selecciona un medio") }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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
        form.errors["depositadoEn"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(Modifier.height(20.dp))

        Text("Fecha Realizada:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 👇 usa formatFecha() declarado en IngresoScreen.kt (mismo package)
            Text("Fecha: ${formatFecha(dateMillis)}", modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showDatePicker = true }) { Text("Cambiar fecha") }
        }
        form.errors["fecha"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

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

        Button(
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }

        val message = message
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
