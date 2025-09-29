package com.example.appfirst.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appfirst.utils.exportDatabase
import com.example.appfirst.utils.importDatabase

@Composable
fun AjustesScreen(
    navigateBack: () -> Unit,
    context: Context // Necesitamos el contexto para llamar a las funciones de exportar/importar
) {
    // Lista de historial de exportaciones (se guarda en memoria para este ejemplo)
    var exportHistory by remember { mutableStateOf(getExportHistory(context)) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Row para el texto y el icono de la flecha hacia atrás
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navigateBack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajustes", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Column para centrar los botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Botón para exportar la base de datos
            Button(
                onClick = {
                    exportDatabase(context)
                    exportHistory = getExportHistory(context) // Actualizar el historial
                    Toast.makeText(context, "Base de datos exportada exitosamente", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Exportar Base de Datos")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón para importar la base de datos
            Button(
                onClick = {
                    if (exportHistory.isNotEmpty()) {
                        // Importar el archivo seleccionado del historial
                        importDatabase(context, exportHistory.last())  // Importa el más reciente
                        Toast.makeText(context, "Base de datos importada exitosamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No hay exportaciones previas.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Importar Base de Datos")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón para volver
            Button(
                onClick = navigateBack,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Volver")
            }
        }

        // Mostrar el historial de exportaciones con padding en la parte inferior
        Spacer(modifier = Modifier.height(20.dp))
        Text("Historial de Exportaciones", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp) // Agregar padding en la parte inferior
        ) {
            items(exportHistory.size) { index ->
                val exportItem = exportHistory[index]
                ExportItemView(exportItem, context, exportHistory)  // Pasa el nombre del archivo aquí
            }
        }
    }
}

@Composable
fun ExportItemView(exportItem: String, context: Context, exportHistory: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable() {
                // Al hacer clic, importar la base de datos seleccionada
                importDatabase(context, exportItem)  // Aquí pasa el nombre del archivo seleccionado
                Toast.makeText(context, "Importando base de datos: $exportItem", Toast.LENGTH_SHORT).show()
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exportItem,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
            )

            IconButton(
                onClick = {
                    // Eliminar el archivo del historial
                    val updatedHistory = exportHistory.filterNot { it == exportItem }
                    saveExportHistory(context, updatedHistory)
                    Toast.makeText(context, "Exportación eliminada", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

fun saveExportHistory(context: Context, history: List<String>) {
    // Guardar el historial actualizado en SharedPreferences
    val prefs = context.getSharedPreferences("ExportHistory", Context.MODE_PRIVATE)
    prefs.edit().putStringSet("history", history.toSet()).apply()
}

fun getExportHistory(context: Context): List<String> {
    // Recuperar el historial de exportaciones
    val prefs = context.getSharedPreferences("ExportHistory", Context.MODE_PRIVATE)
    val history = prefs.getStringSet("history", emptySet())?.toList() ?: emptyList()
    return history
}
