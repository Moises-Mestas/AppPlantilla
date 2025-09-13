package com.example.appfirst.ui.screens.calendar

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModelFactory
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaNota
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaDetallesDiaScreen(
    fecha: String,
    navController: NavController,
    onBackToCalendario: () -> Unit,
) {
    val viewModel: NotaViewModel = viewModel(
        factory = NotaViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    val notasState by viewModel.notasState.collectAsState()

    LaunchedEffect(fecha) {
        if (fecha.isNotEmpty()) {
            viewModel.cargarNotasPorFecha(fecha)
        }
    }

    var seccionActiva by remember { mutableStateOf("Notas") }

    fun navigateToDetails(fecha: String) {
        navController.navigate("detalles-dia/$fecha")
    }

    fun navigateToEditNota(notaId: Int) {
        navController.navigate("editar-nota/$notaId")
    }

    fun navigateToAddNota() {
        navController.navigate("nueva-nota/$fecha")
    }

    fun eliminarNota(id: Int) {
        viewModel.eliminarNota(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles del día",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToCalendario) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al calendario"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToAddNota() }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir evento")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Encabezado con fecha
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatearFechaDetalles(fecha),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${notasState.size} evento(s) programado(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Selector de sección
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = seccionActiva == "Notas",
                    onClick = { seccionActiva = "Notas" },
                    label = { Text("Eventos") },
                    leadingIcon = {
                        if (seccionActiva == "Notas") {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = seccionActiva == "Movimientos",
                    onClick = { seccionActiva = "Movimientos" },
                    label = { Text("Movimientos") },
                    leadingIcon = {
                        if (seccionActiva == "Movimientos") {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Contenido de la sección seleccionada
            when (seccionActiva) {
                "Notas" -> {
                    if (notasState.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Sin eventos",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No hay eventos para este día",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Haz clic en el botón + para agregar uno",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { navigateToAddNota() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Añadir",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Crear primer evento")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notasState) { nota ->
                                TarjetaNota(
                                    nota = nota,
                                    onEditar = { navigateToEditNota(nota.id) },
                                    onEliminar = { eliminarNota(nota.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                "Movimientos" -> {
                    SeccionMovimientosDemo()
                }
            }
        }
    }
}

fun formatearFechaDetalles(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatoEntrada.parse(fecha)
        val formatoSalida = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale.getDefault())
        formatoSalida.format(date ?: return fecha)
    } catch (e: Exception) {
        fecha
    }
}

@Composable
fun SeccionMovimientosDemo() {
    val movimientosTextoPlano = listOf(
        "+ S/ 50.00 (Ingreso - Salario)",
        "- S/ 20.00 (Gasto - Comida)",
        "- S/ 5.00 (Gasto - Transporte)"
    )
    val balance = 25.00

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Balance del día: S/ ${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Modo demostración - Datos de ejemplo",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Movimientos del día:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            movimientosTextoPlano.forEach { movimiento ->
                Text(
                    movimiento,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = when {
                        movimiento.startsWith("+") -> Color(0xFF4CAF50) // Verde
                        movimiento.startsWith("-") -> Color(0xFFF44336) // Rojo
                        else -> MaterialTheme.colorScheme.onBackground
                    }
                )
            }
        }
    }
}
