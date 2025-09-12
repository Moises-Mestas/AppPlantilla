package com.example.appfirst.ui.screens.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import android.app.Application
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaAccionDiaria
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.ui.screens.calendar.elementos.AccionDiariaViewModelFactory
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaNotaHorario
import androidx.compose.runtime.collectAsState
import com.example.appfirst.ui.screens.calendar.elementos.TarjetaNota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioDiarioScreen(
    onBack: () -> Unit,
    onEditarAccion: (AccionDiaria?) -> Unit,
    onAddNota: () -> Unit
) {
    val viewModel: AccionDiariaViewModel = viewModel(
        factory = AccionDiariaViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )

    // Estados para los filtros (solo afectan a acciones)
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroDia by remember { mutableStateOf("Todos") }
    var filtroCategoria by remember { mutableStateOf("Todas") }

    // Estados del ViewModel
    val accionesFiltradas by viewModel.accionesFiltradas.collectAsState()
    val horarioEstado by viewModel.horarioEstado.collectAsState()
    val diaHoy = viewModel.obtenerDiaDeLaSemanaHoy()

    // Aplicar filtros cuando cambien
    LaunchedEffect(textoBusqueda, filtroDia, filtroCategoria) {
        viewModel.setFiltros(textoBusqueda, filtroDia, filtroCategoria)
    }

    // Cargar horario cuando se abre la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarHorarioDeHoy()
    }

    val diasSemana = listOf("Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val categorias = listOf("Todas", "Trabajo", "Estudio", "Ejercicio", "Personal", "Salud", "Otros")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mi Horario")
                        Text(
                            diaHoy,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditarAccion(null) }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir acción")
                    }
                    IconButton(onClick = onAddNota) {
                        Icon(Icons.Default.MailOutline, contentDescription = "Añadir nota")
                    }
                    IconButton(onClick = {
                        viewModel.cargarHorarioDeHoy()
                        // Resetear filtros al actualizar
                        textoBusqueda = ""
                        filtroDia = "Todos"
                        filtroCategoria = "Todas"
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
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
                    label = { Text("Buscar acciones...") },
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
                            contentDescription = "Limpiar filtros",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // FILTRO POR DÍA
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

            // FILTRO POR CATEGORÍA
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

            // CONTENIDO PRINCIPAL
            when {
                horarioEstado.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                horarioEstado.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Error",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error al cargar datos",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // SECCIÓN DE ACCIONES (FILTRADAS)
                        if (accionesFiltradas.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Acciones (${accionesFiltradas.size})",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }

                            items(accionesFiltradas) { accion ->
                                TarjetaAccionDiaria(
                                    accion = accion,
                                    onEditar = { onEditarAccion(accion) },
                                    onEliminar = { viewModel.eliminarAccion(accion) }
                                )
                            }
                        } else if (textoBusqueda.isNotBlank() || filtroDia != "Todos" || filtroCategoria != "Todas") {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay acciones con estos filtros",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // SECCIÓN DE NOTAS (SIEMPRE SE MUESTRAN LAS DE HOY)
                        if (horarioEstado.notas.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Notas de Hoy (${horarioEstado.notas.size})",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }

                            items(horarioEstado.notas) { nota ->
                                TarjetaNotaHorario(nota = nota)
                            }
                        }

                        // MENSAJE SI NO HAY NADA
                        if (accionesFiltradas.isEmpty() &&
                            (textoBusqueda.isBlank() && filtroDia == "Todos" && filtroCategoria == "Todas") &&
                            horarioEstado.notas.isEmpty()) {

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Sin actividades",
                                            modifier = Modifier.size(48.dp),
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No hay actividades programadas\nni notas para hoy",
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}