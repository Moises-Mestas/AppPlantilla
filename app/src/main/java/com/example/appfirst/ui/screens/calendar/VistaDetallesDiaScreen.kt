package com.example.appfirst.ui.screens.calendar

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaNota
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun VistaDetallesDiaScreen(
    fecha: String,
    onBackToCalendario: () -> Unit
) {
    val viewModel: NotaViewModel = viewModel(
        factory = NotaViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    // Para debug
    LaunchedEffect(fecha) {
        Log.d("VistaDetalles", "Fecha recibida para buscar: $fecha")
    }
    // FORMA CORRECTA: Usar LiveData con observeAsState
    val notas by viewModel.obtenerNotasPorFecha(fecha).observeAsState(emptyList())

    // DEBUG: Ver todas las notas en la BD para diagnosticar
    val todasLasNotas by viewModel.obtenerTodasLasNotas().observeAsState(emptyList())
    LaunchedEffect(todasLasNotas) {
        Log.d("VistaDetalles", "Todas las notas en BD: ${todasLasNotas.size}")
        todasLasNotas.forEach { nota ->
            Log.d("VistaDetalles", "Nota en BD: ${nota.titulo} - ${nota.fecha}")
        }
    }

    var seccionActiva by remember { mutableStateOf("Notas") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Botón para volver
        Button(onClick = onBackToCalendario) {
            Text("← Calendario")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Título con fecha
        Text(
            text = "Detalles de ${formatearFechaDetalles(fecha)}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold
        )

        // DEBUG: Mostrar la fecha que se está usando
        Text(
            text = "Buscando notas para: $fecha",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { seccionActiva = "Notas" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seccionActiva == "Notas") MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("Notas", color = Color.White)
            }

            Button(
                onClick = { seccionActiva = "Movimientos" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seccionActiva == "Movimientos") MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("Movimientos", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (seccionActiva) {
            "Notas" -> {
                if (notas.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay eventos programados para esta fecha.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Fecha buscada: $fecha",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn {
                        items(notas) { nota ->
                            TarjetaNota(nota = nota)
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
                        movimiento.startsWith("+") -> Color.Green
                        movimiento.startsWith("-") -> Color.Red
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