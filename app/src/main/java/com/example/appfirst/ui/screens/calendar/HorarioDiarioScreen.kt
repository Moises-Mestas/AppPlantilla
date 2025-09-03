package com.example.appfirst.ui.screens.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import android.app.Application
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaAccionDiaria
import androidx.compose.material.icons.filled.*

@Composable
fun HorarioDiarioScreen(
    onBack: () -> Unit,
    onEditarAccion: (AccionDiaria?) -> Unit
) {
    val viewModel: AccionDiariaViewModel = viewModel(
        factory = AccionDiariaViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )

    // Estados para los filtros
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroDia by remember { mutableStateOf("Todos") }
    var filtroCategoria by remember { mutableStateOf("Todas") }

    val acciones by viewModel.accionesFiltradas.observeAsState(emptyList())

    // Aplicar filtros cuando cambien
    LaunchedEffect(textoBusqueda, filtroDia, filtroCategoria) {
        viewModel.setFiltros(textoBusqueda, filtroDia, filtroCategoria)
    }

    val diasSemana = listOf("Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val categorias = listOf("Todas", "Trabajo", "Estudio", "Ejercicio", "Personal", "Salud", "Otros")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp)
    ) {
        // Header simple
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Mi Horario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { onEditarAccion(null) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir acción",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BARRA DE BÚSQUEDA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar por título o descripción") },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (textoBusqueda.isNotEmpty()) {
                        IconButton(
                            onClick = { textoBusqueda = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true
            )

            // BOTÓN PARA LIMPIAR TODOS LOS FILTROS
            if (textoBusqueda.isNotEmpty() || filtroDia != "Todos" || filtroCategoria != "Todas") {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        textoBusqueda = ""
                        filtroDia = "Todos"
                        filtroCategoria = "Todas"
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Limpiar todos los filtros",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // FILTRO POR DÍA - Botones deslizables
        Text(
            text = "Filtrar por día:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diasSemana) { dia ->
                FilterChip(
                    selected = filtroDia == dia,
                    onClick = { filtroDia = dia },
                    label = { Text(dia) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // FILTRO POR CATEGORÍA - Botones deslizables
        Text(
            text = "Filtrar por categoría:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->
                FilterChip(
                    selected = filtroCategoria == categoria,
                    onClick = { filtroCategoria = categoria },
                    label = { Text(categoria) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Contador de resultados
        Text(
            text = "Encontradas: ${acciones.size} acciones",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // LISTA DE ACCIONES
        if (acciones.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Sin acciones",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (textoBusqueda.isNotBlank() || filtroDia != "Todos" || filtroCategoria != "Todas") {
                        "No hay acciones con estos filtros"
                    } else {
                        "No hay acciones programadas"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(acciones) { accion ->
                    TarjetaAccionDiaria(
                        accion = accion,
                        onEditar = { onEditarAccion(accion) },
                        onEliminar = { viewModel.eliminarAccion(accion) }
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioAccionDiariaScreen(
    accionExistente: AccionDiaria? = null,
    onGuardar: (AccionDiaria) -> Unit,
    onCancelar: () -> Unit
) {
    // Estados corregidos usando remember con mutableStateOf
    var titulo by remember { mutableStateOf(accionExistente?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(accionExistente?.descripcion ?: "") }
    var horaInicio by remember { mutableStateOf(accionExistente?.horaInicio ?: "08:00") }
    var horaFin by remember { mutableStateOf(accionExistente?.horaFin ?: "09:00") }
    var color by remember { mutableStateOf(accionExistente?.color ?: 0xFF2196F3.toInt()) }
    var categoria by remember { mutableStateOf(accionExistente?.categoria ?: "Personal") }
    var prioridad by remember { mutableStateOf(accionExistente?.prioridad ?: 3) }

    // ✅ CORRECCIÓN: Usar mutableStateOf con Set<String> en lugar de mutableStateSetOf
    var diasSeleccionados by remember {
        mutableStateOf(
            accionExistente?.diasSemana?.split(",")?.toSet() ?: setOf("Todos")
        )
    }

    val colores = listOf(
        0xFF2196F3.toInt(), 0xFF4CAF50.toInt(), 0xFFFFC107.toInt(),
        0xFFF44336.toInt(), 0xFF9C27B0.toInt()
    )

    val categorias = listOf("Trabajo", "Estudio", "Ejercicio", "Personal", "Salud", "Otros")
    val todosLosDias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (accionExistente == null) "Nueva Acción Diaria" else "Editar Acción",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            colores.forEach { colorOption ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(colorOption))
                        .border(
                            if (color == colorOption) 3.dp else 1.dp,
                            if (color == colorOption) MaterialTheme.colorScheme.primary else Color.Gray,
                            CircleShape
                        )
                        .clickable { color = colorOption }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ CORRECCIÓN MEJORADA: Selector de días múltiples
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
            items(categorias) { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { categoria = cat },
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
                    // Convertir Set a String separado por comas
                    val diasString = if (diasSeleccionados.isEmpty()) {
                        "Todos"
                    } else {
                        diasSeleccionados.joinToString(",")
                    }

                    val accion = AccionDiaria(
                        id = accionExistente?.id ?: 0,
                        titulo = titulo,
                        descripcion = descripcion,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        color = color,
                        diasSemana = diasString,
                        categoria = categoria,
                        prioridad = prioridad,
                        esPermanente = true
                    )
                    onGuardar(accion)
                },
                enabled = titulo.isNotBlank() &&
                        horaInicio.isNotBlank() &&
                        horaFin.isNotBlank() &&
                        diasSeleccionados.isNotEmpty()
            ) {
                Text(if (accionExistente == null) "Crear" else "Actualizar")
            }
        }
    }
}