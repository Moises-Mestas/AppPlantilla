
package com.example.appfirst.ui.ingresos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.screens.home.NavDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navigateToCuentas: () -> Unit,
    navigateToFormIngreso2: () -> Unit,
    navigateToFormGasto: () -> Unit,
    navigateBack: () -> Unit,
    navigateToHistorial: () -> Unit,
    navigateToEditIngreso: (Int) -> Unit,
    navigateToEditGasto: (Int) -> Unit
) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current

    val ingresos by viewModel.ingresos.collectAsState()
    val montoTotalTarjeta by viewModel.montoTotalTarjeta.collectAsState()
    val montoTotalEfectivo by viewModel.montoTotalEfectivo.collectAsState()
    val montoTotalYape by viewModel.montoTotalYape.collectAsState()
    val totalTransactions = viewModel.getTotalTransactionsCount()


    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf(0) }
    var open by remember { mutableStateOf(false) }

    // Filtros
    var fechaSeleccionada by remember { mutableStateOf<Long?>(null) }
    var fechaSeleccionada2 by remember { mutableStateOf<Long?>(null) }
    var selectedPaymentType by remember { mutableStateOf("TOTAL") }


    var pendingDelete by remember { mutableStateOf<Ingreso?>(null) }

    var isAscending by remember { mutableStateOf(true) }

    var isFilteredByAmount by remember { mutableStateOf(false) }

    // Cargar userId
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

    // Aplicar filtros y orden

    var filteredIngresos = ingresos
        .filter {
            (fechaSeleccionada == null || it.fecha >= fechaSeleccionada!!) &&
                    (fechaSeleccionada2 == null || it.fecha <= fechaSeleccionada2!!)
        }
        .filter {
            when (selectedPaymentType) {
                "TARJETA"  -> it.depositadoEn == com.example.appfirst.data.local.entity.MedioPago.TARJETA
                "EFECTIVO" -> it.depositadoEn == com.example.appfirst.data.local.entity.MedioPago.EFECTIVO
                "YAPE"     -> it.depositadoEn == com.example.appfirst.data.local.entity.MedioPago.YAPE
                else       -> true
            }
        }
        .sortedByDescending { it.fecha }

    if (isFilteredByAmount) {
        filteredIngresos = filteredIngresos.sortedBy {
            if (isAscending) it.monto else -it.monto
        }
    }

    val totalMonto = when (selectedPaymentType) {
        "TARJETA"  -> montoTotalTarjeta
        "EFECTIVO" -> montoTotalEfectivo
        "YAPE"     -> montoTotalYape
        else       -> montoTotalTarjeta + montoTotalEfectivo + montoTotalYape
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // Fondo blanco
                    titleContentColor = Color.Black, // Título negro
                ),
                title = {
                    Text("-+-+ Historial +-+-", fontWeight = FontWeight.Bold,fontSize = 25.sp) // Texto más pequeño
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.Filled.ArrowBack, // Ícono de flecha
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
                            if (index == 3) navigateToCuentas()
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
                .padding(8.dp)
        ) {
            // Títulos
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "Monto Total",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 1.dp, bottom = 4.dp)
                )
            }

            Text(
                text = "S/ ${"%.2f".format(totalMonto)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Selector TOTAL/TARJETA/EFECTIVO/YAPE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val options = listOf("TOTAL", "TARJETA", "EFECTIVO", "YAPE")
                options.forEach { option ->
                    Button(
                        onClick = { selectedPaymentType = option },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedPaymentType == option)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            option,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Filtros por fecha: desde / hasta
// Filtros por fecha: desde / hasta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Alinea los elementos de los extremos
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna de filtros por fecha (izquierda)
                Column(
                    modifier = Modifier.weight(1f), // Hace que ocupe el espacio disponible
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre los filtros
                    horizontalAlignment = Alignment.Start // Alinea los filtros a la izquierda
                ) {
                    // Filtro de fecha desde
                    FechaSeleccionadaSection1(
                        fecha = fechaSeleccionada ?: System.currentTimeMillis(),
                        onFechaChange = { nueva -> fechaSeleccionada = nueva },
                        modifier = Modifier.padding(start = 12.dp) // Mover un poco la fecha hacia la derecha
                    )

                    // Filtro de fecha hasta
                    FechaSeleccionadaSection1(
                        fecha = fechaSeleccionada2 ?: System.currentTimeMillis(),
                        onFechaChange = { nueva -> fechaSeleccionada2 = nueva },
                        modifier = Modifier.padding(start = 12.dp) // Mover un poco la fecha hacia la derecha
                    )
                }

                // Columna de iconos (derecha)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 5.dp) // Ajusta el padding aquí para mover las flechas a la derecha o izquierda
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            isFilteredByAmount = true
                            isAscending = false
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(45.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = "Subir",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            isFilteredByAmount = true
                            isAscending = true
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(45.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowDownward,
                            contentDescription = "Bajar",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }



            RestablecerButton {
                fechaSeleccionada = null
                fechaSeleccionada2 = null
                isFilteredByAmount = false
                isAscending = true
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start, // Alineación a la izquierda
                verticalAlignment = Alignment.CenterVertically // Alineación vertical al centro
            ) {
                Text(
                    text = "Transacciones recientes: ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(8.dp)) // Espacio entre el texto y el número de transacciones

                Text(
                    text = "$totalTransactions T",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Divider(Modifier.padding(top = 4.dp))
            // Lista
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
                filteredIngresos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ingresos. ¡Agrega uno desde el botón!")
                }
                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredIngresos) { ingreso ->
                        IngresoItemSimple(
                            ingreso = ingreso,
                            onClick = {
                                val id = ingreso.id
                                if (ingreso.monto < 0) navigateToEditGasto(id) else navigateToEditIngreso(id)
                            },
                            onDelete = { pendingDelete = ingreso }
                        )
                    }
                }
            }

            // Diálogo de confirmación (queda fuera de la LazyColumn)
            pendingDelete?.let { p ->
                AlertDialog(
                    onDismissRequest = { pendingDelete = null },
                    title = { Text("Eliminar ${if (p.monto < 0) "gasto" else "ingreso"}") },
                    text = {
                        Text(
                            "¿Seguro que deseas eliminar \"${p.descripcion}\" por S/ " +
                                    "%.2f".format(kotlin.math.abs(p.monto)) +
                                    "? Esta acción no se puede deshacer."
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.delete(p.id)
                            pendingDelete = null
                        }) { Text("Eliminar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") }
                    }
                )
            }
        }


        AddFabWithSheet3(
            sheetOffsetY = -20.dp,
            bottomPadding = innerPadding.calculateBottomPadding(),
            open = open,
            onOpenChange = { open = it },
            navigateToGastos = navigateToFormGasto,
            navigateToHistorial = navigateToHistorial,
            navigateToIngreso = navigateToFormIngreso2
        )

        if (!open) {
            // Aquí es donde se coloca el botón de Historial y el botón Add en la misma altura
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

                FloatingActionButton(
                    onClick = { open = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 155.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }




    MovableArrowButtons(
        onArrowUpClick = {
            isFilteredByAmount = true
            isAscending = false
        },
        onArrowDownClick = {
            isFilteredByAmount = true
            isAscending = true
        },
        onMoneyIconClick = {

        }
    )

}







/* -------------------------- Helpers fuera de la pantalla -------------------------- */

@Composable
fun IngresoItemSimple(
    ingreso: Ingreso,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val backgroundColor = if (ingreso.monto < 0) {
        androidx.compose.ui.graphics.Color(0xFFFFCDD2) // rojo claro
    } else {
        androidx.compose.ui.graphics.Color(0xFFC8E6C9) // verde claro
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp) // Reducir la altura del card
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .padding(14.dp) // Reducir el padding general
            ) {

                Column(modifier = Modifier.weight(0.8f)) {
                    Text(
                        text = "Fecha: ${formatFecha(ingreso.fecha)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    val truncatedDescription = if (ingreso.descripcion.length > 8) {
                        "${ingreso.descripcion.take(10)}..."
                    } else {
                        ingreso.descripcion
                    }
                    Text(
                        text = "Descripción: $truncatedDescription",
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Depositado en: ${ingreso.depositadoEn}",
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Categoría: ${ingreso.notas.display()}",
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(Modifier.width(20.dp))

                // Alineamos la columna del monto a la izquierda
                Column(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight()
                        .padding(top = 32.dp)
                        .wrapContentWidth(Alignment.Start) // Alinea el contenido de la columna a la izquierda
                ) {
                    Text(
                        text = "S/ ${"%.2f".format(ingreso.monto)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Botón de eliminación
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {

                Box(
                    modifier = Modifier
                        .background(Color(0xFFF44336), shape = CircleShape)
                        .padding(1.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Eliminar",
                        tint = Color.White
                    )
                }
            }

        }
    }
}






@Composable
fun RestablecerButton(onReset: () -> Unit) {
    Button(
        onClick = onReset,
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxWidth()
            .height(35.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = "RESTABLECER",
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 18.sp
        )
    }
}

@Composable
fun FechaSeleccionadaSection1(
    fecha: Long,
    onFechaChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val formattedFecha = formatFecha(fecha)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier // Usar el modifier que recibe la función
    ) {
        Text("Fecha: $formattedFecha", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .padding(start = 4.dp)
                .widthIn(min = 90.dp)
                .height(40.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) { Text("CAMBIAR") }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            initialDate = Calendar.getInstance().apply { timeInMillis = fecha },
            onDateSelected = { year, month, day ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = fecha
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                onFechaChange(cal.timeInMillis)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}


fun formatFecha(timestamp: Long): String = try {
    android.text.format.DateFormat.format("dd/MM/yy HH:mm", Date(timestamp)).toString()
} catch (e: Exception) {
    "Fecha inválida"
}



@Composable
fun AddFabWithSheet3(
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
fun MovableArrowButtons(
    onArrowUpClick: () -> Unit,
    onArrowDownClick: () -> Unit,
    onMoneyIconClick: () -> Unit
) {
    // Ya no es necesario el contenido de esta función
}