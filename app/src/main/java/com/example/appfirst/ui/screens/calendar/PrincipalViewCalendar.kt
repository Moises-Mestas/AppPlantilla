package com.example.appfirst.ui.screens.calendar

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat


@Composable
fun CalendarioScreen(
    onNavigateToInicio: () -> Unit,
    onNavigateToDetalles: (String) -> Unit,
    onNavigateToNota: (String) -> Unit  // Cambiado para recibir fecha
) {
    var selectedDate by rememberSaveable { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    // Obtener fecha actual formateada
    val fechaActual = remember {
        val today = Calendar.getInstance()
        formatearFechaParaBD(
            today.get(Calendar.DAY_OF_MONTH),
            today.get(Calendar.MONTH),
            today.get(Calendar.YEAR)
        )
    }

    // Inicializar con fecha actual si no hay selección
    if (selectedDate.isEmpty()) {
        selectedDate = fechaActual
    }

    // Función para navegar a detalles con la fecha seleccionada
    fun navigateToDetails() {
        onNavigateToDetalles(selectedDate)
    }

    // Función para añadir nota con fecha seleccionada
    fun navigateToAddNota() {
        onNavigateToNota(selectedDate)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Header mejorado (similar a HorarioDiarioScreen)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateToInicio,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver al inicio",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Calendario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Espacio para mantener la simetría
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Contenedor principal del calendario
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 80.dp)
        ) {
            // Selector de mes y año mejorado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        calendar.set(currentYear, currentMonth, 1)
                        calendar.add(Calendar.MONTH, -1)
                        currentMonth = calendar.get(Calendar.MONTH)
                        currentYear = calendar.get(Calendar.YEAR)
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
                }

                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        .format(calendar.apply { set(currentYear, currentMonth, 1) }.time),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = {
                        calendar.set(currentYear, currentMonth, 1)
                        calendar.add(Calendar.MONTH, 1)
                        currentMonth = calendar.get(Calendar.MONTH)
                        currentYear = calendar.get(Calendar.YEAR)
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana con mejor estilo
            val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Días del mes
            val daysInMonth = calendar.apply { set(currentYear, currentMonth, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfMonth = calendar.apply { set(currentYear, currentMonth, 1) }.get(Calendar.DAY_OF_WEEK)

            // Ajustar para que la semana comience en lunes
            val offset = if (firstDayOfMonth == Calendar.SUNDAY) 6 else firstDayOfMonth - Calendar.MONDAY

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(300.dp)
            ) {
                items(offset) {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                }

                items(daysInMonth) { day ->
                    val dayNumber = day + 1
                    val fechaFormateada = formatearFechaParaBD(dayNumber, currentMonth, currentYear)
                    val isSelected = selectedDate == fechaFormateada
                    val isToday = fechaFormateada == fechaActual

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                    else -> Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .border(
                                when {
                                    isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    isToday -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                                    else -> BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f))
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                selectedDate = fechaFormateada
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayNumber.toString(),
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onBackground
                                },
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fecha seleccionada y botones de acción
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatearFechaLegible(selectedDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botón para ver detalles
                        Button(
                            onClick = { navigateToDetails() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ver detalles",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Detalles")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Botón para añadir nota
                        Button(
                            onClick = { navigateToAddNota() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir nota",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Añadir")
                        }
                    }
                }
            }
        }
    }
}

fun formatearFechaLegible(fecha: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatoEntrada.parse(fecha)
        formatoSalida.format(date)
    } catch (e: Exception) {
        fecha
    }
}

fun isHoy(dia: Int, mes: Int, año: Int): Boolean {
    val calendar = Calendar.getInstance()
    return dia == calendar.get(Calendar.DAY_OF_MONTH) &&
            mes == calendar.get(Calendar.MONTH) &&
            año == calendar.get(Calendar.YEAR)
}

fun formatearFechaParaBD(dia: Int, mes: Int, año: Int): String {
    val mesFormateado = (mes + 1).toString().padStart(2, '0')
    val diaFormateado = dia.toString().padStart(2, '0')
    return "$año-$mesFormateado-$diaFormateado"
}