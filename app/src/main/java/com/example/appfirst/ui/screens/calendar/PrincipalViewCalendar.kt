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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
    onNavigateToNota: () -> Unit
) {
    var selectedDate by rememberSaveable { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    // Obtener el contexto
    val context = LocalContext.current

    // Función para navegar a detalles con la fecha seleccionada
    fun navigateToDetails() {
        if (selectedDate.isNotEmpty()) {
            onNavigateToDetalles(selectedDate)
        } else {
            Toast.makeText(context, "Seleccione una fecha primero", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Botón superior izquierdo
        Button(
            onClick = onNavigateToInicio,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("← Inicio")
        }

        // Contenedor principal del calendario
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 60.dp)
        ) {
            // Selector de mes y año
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
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Mes anterior")
                }

                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        .format(calendar.apply { set(currentYear, currentMonth, 1) }.time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        calendar.set(currentYear, currentMonth, 1)
                        calendar.add(Calendar.MONTH, 1)
                        currentMonth = calendar.get(Calendar.MONTH)
                        currentYear = calendar.get(Calendar.YEAR)
                    }
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Mes siguiente")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana
            val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
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

            LazyVerticalGrid(columns = GridCells.Fixed(7)) {
                items(daysInMonth) { day ->
                    val dayNumber = day + 1
                    val fechaFormateada = formatearFechaParaBD(dayNumber, currentMonth, currentYear)
                    val isSelected = selectedDate == fechaFormateada
                    val isToday = isHoy(dayNumber, currentMonth, currentYear)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .border(
                                when {
                                    isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    isToday -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                                    else -> BorderStroke(0.dp, Color.Transparent)
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                selectedDate = fechaFormateada
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isToday -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onBackground
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha seleccionada
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedDate.isNotEmpty()) "Fecha seleccionada: ${formatearFechaLegible(selectedDate)}"
                    else "Seleccione una fecha",
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { navigateToDetails() },
                    enabled = selectedDate.isNotEmpty()
                ) {
                    Text("Ver Detalles")
                }
            }
        }

        //  añadir nota
        Button(
            onClick = { onNavigateToNota() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Añadir nota")
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