package com.example.appfirst.ui.screens.calendar

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModelFactory
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaNota
import java.text.SimpleDateFormat
import java.util.Locale
//import kotlinx.coroutines.flow.collectAsState

@Composable
fun VistaDetallesDiaScreen(
    fecha: String,
    onBackToCalendario: () -> Unit,
    onAddNota: (String) -> Unit,
    onEditarNota: (Int) -> Unit
) {
    val viewModel: NotaViewModel = viewModel(
        factory = NotaViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    // OBSERVA el StateFlow usando collectAsState() en lugar de observeAsState()
    val notas by viewModel.notas.collectAsState()

    // Carga las notas cuando la pantalla se muestra o cambia la fecha
    LaunchedEffect(fecha) {
        viewModel.cargarNotasPorFecha(fecha)
    }

    var seccionActiva by remember { mutableStateOf("Notas") }

    // Función para eliminar nota - corregido el tipo de parámetro
    fun eliminarNota(id: Int) {
        if (id > 0) {
            viewModel.eliminarNota(id)
        } else {
            Log.e("VistaDetalles", "No se puede eliminar nota sin ID")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp)
    ) {
        // Header mejorado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToCalendario,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al calendario",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Detalles del día",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { onAddNota(fecha) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir nota",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fecha
        Text(
            text = formatearFechaDetalles(fecha),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Filtros de sección (mejorados)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = seccionActiva == "Notas",
                onClick = { seccionActiva = "Notas" },
                label = { Text("Eventos") },
                leadingIcon = {
                    if (seccionActiva == "Notas") {
                        Icon(Icons.Default.Check, contentDescription = "Seleccionado")
                    }
                }
            )

            FilterChip(
                selected = seccionActiva == "Movimientos",
                onClick = { seccionActiva = "Movimientos" },
                label = { Text("Movimientos") },
                leadingIcon = {
                    if (seccionActiva == "Movimientos") {
                        Icon(Icons.Default.Check, contentDescription = "Seleccionado")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (seccionActiva) {
            "Notas" -> {
                if (notas.isEmpty()) {
                    // Mensaje cuando no hay notas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay eventos para este día",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notas) { nota ->
                            // Asegúrate de que nota.id no sea null
                            val notaId = nota.id ?: 0

                            TarjetaNota(
                                nota = nota,
                                onEditar = {
                                    if (notaId > 0) {
                                        onEditarNota(notaId)
                                    }
                                },
                                onEliminar = { eliminarNota(notaId) } // Pasa el ID en lugar de la nota
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

fun formatearFechaDetalles(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatoEntrada.parse(fecha)
        val formatoSalida = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        formatoSalida.format(date)
    } catch (e: Exception) {
        try {
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formatoEntrada.parse(fecha)
            val formatoSalida = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            formatoSalida.format(date)
        } catch (e: Exception) {
            fecha
        }
    }
}