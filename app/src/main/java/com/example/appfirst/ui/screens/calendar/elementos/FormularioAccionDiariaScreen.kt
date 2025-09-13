package com.example.appfirst.ui.screens.calendar.elementos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.ui.screens.calendar.AccionDiariaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioAccionDiariaScreen(
    navController: NavController,
    accionId: Int,
    viewModel: AccionDiariaViewModel,
    onGuardar: (AccionDiaria) -> Unit,
    onCancelar: () -> Unit,

    categories: List<String> = listOf("Trabajo", "Estudio", "Ejercicio", "Personal", "Salud", "Otros"),
    colors: List<Int> = listOf(
        0xFF2196F3.toInt(), 0xFF4CAF50.toInt(), 0xFFFFC107.toInt(),
        0xFFF44336.toInt(), 0xFF9C27B0.toInt()    ),
    diasSemana: List<String> = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
) {
    // Estados del formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var horaFin by remember { mutableStateOf("09:00") }
    var colorSeleccionado by remember { mutableStateOf(colors.first()) }
    var categoriaSeleccionada by remember { mutableStateOf(categories.first()) }
    var prioridad by remember { mutableStateOf(3) }
    var diasSeleccionados by remember { mutableStateOf(setOf("Todos")) }
    var isLoading by remember { mutableStateOf(accionId > 0) }

    LaunchedEffect(accionId) {
        if (accionId > 0) {
            try {
                val accionExistente = viewModel.obtenerAccionPorId(accionId)
                accionExistente?.let { accion ->
                    titulo = accion.titulo
                    descripcion = accion.descripcion
                    horaInicio = accion.horaInicio
                    horaFin = accion.horaFin
                    colorSeleccionado = accion.color
                    categoriaSeleccionada = accion.categoria
                    prioridad = accion.prioridad
                    diasSeleccionados = if (accion.diasSemana.isNotEmpty() && accion.diasSemana != "Todos") {
                        accion.diasSemana.split(",").toSet()
                    } else {
                        setOf("Todos")
                    }
                }
            } catch (e: Exception) {
                // Manejar error
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val colores = listOf(
        0xFF2196F3.toInt(), 0xFF4CAF50.toInt(), 0xFFFFC107.toInt(),
        0xFFF44336.toInt(), 0xFF9C27B0.toInt()
    )

    val categorias = listOf("Trabajo", "Estudio", "Ejercicio", "Personal", "Salud", "Otros")
    val todosLosDias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (accionId == 0) "Nueva Acción" else "Editar Acción",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onCancelar() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (accionId > 0) {
                        IconButton(
                            onClick = {
                                if (accionId > 0) {
                                    viewModel.eliminarAccionPorId(accionId)
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Campos del formulario
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título de la acción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de horas
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora inicio:", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { horaInicio = it },
                            placeholder = { Text("HH:mm") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora fin:", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { horaFin = it },
                            placeholder = { Text("HH:mm") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de color
                Text("Color:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colors.forEach { colorOption ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(colorOption))
                                .border(
                                    if (colorSeleccionado == colorOption) 3.dp else 1.dp,
                                    if (colorSeleccionado == colorOption) MaterialTheme.colorScheme.primary else Color.Gray,
                                    CircleShape
                                )
                                .clickable { colorSeleccionado = colorOption }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de días múltiples
                Text("Días de la semana:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Opción "Todos"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            diasSeleccionados = if (diasSeleccionados.contains("Todos")) {
                                emptySet()
                            } else {
                                setOf("Todos")
                            }
                        }
                ) {
                    Checkbox(
                        checked = diasSeleccionados.contains("Todos"),
                        onCheckedChange = { checked ->
                            diasSeleccionados = if (checked) {
                                setOf("Todos")
                            } else {
                                emptySet()
                            }
                        }
                    )
                    Text("Todos los días", style = MaterialTheme.typography.bodyMedium)
                }

                // Días individuales (solo si "Todos" no está seleccionado)
                if (!diasSeleccionados.contains("Todos")) {
                    todosLosDias.forEach { dia ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    diasSeleccionados = if (diasSeleccionados.contains(dia)) {
                                        diasSeleccionados - dia
                                    } else {
                                        diasSeleccionados + dia
                                    }
                                }
                        ) {
                            Checkbox(
                                checked = diasSeleccionados.contains(dia),
                                onCheckedChange = { checked ->
                                    diasSeleccionados = if (checked) {
                                        diasSeleccionados + dia
                                    } else {
                                        diasSeleccionados - dia
                                    }
                                }
                            )
                            Text(dia, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de categoría
                Text("Categoría:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = categoriaSeleccionada == cat,
                            onClick = { categoriaSeleccionada = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de prioridad
                Text("Prioridad:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..5).forEach { nivel ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (prioridad == nivel) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { prioridad = nivel }
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nivel.toString(),
                                color = if (prioridad == nivel) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCancelar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val diasString = if (diasSeleccionados.isEmpty()) {
                                "Todos"
                            } else {
                                diasSeleccionados.joinToString(",")
                            }

                            val accion = AccionDiaria(
                                id = accionId,
                                titulo = titulo,
                                descripcion = descripcion,
                                horaInicio = horaInicio,
                                horaFin = horaFin,
                                color = colorSeleccionado,
                                diasSemana = diasString,
                                categoria = categoriaSeleccionada,
                                prioridad = prioridad,
                                esPermanente = true
                            )

                            if (accionId == 0) {
                                onGuardar(accion) // Crear nueva
                            } else {
                                onGuardar(accion) // Editar existente - esto llama a editarAccion
                            }
                        },
                        enabled = titulo.isNotBlank() &&
                                horaInicio.isNotBlank() &&
                                horaFin.isNotBlank() &&
                                diasSeleccionados.isNotEmpty()
                    ) {
                        Text(if (accionId == 0) "Crear" else "Actualizar")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}