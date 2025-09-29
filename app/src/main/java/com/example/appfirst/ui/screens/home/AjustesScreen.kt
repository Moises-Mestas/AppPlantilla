package com.example.appfirst.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appfirst.utils.exportDatabase
import com.example.appfirst.utils.importDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    navigateBack: () -> Unit,
    context: Context // Necesitamos el contexto para llamar a las funciones de exportar/importar
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Ajustes", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para exportar la base de datos
        Button(onClick = {
            exportDatabase(context)
            Toast.makeText(context, "Base de datos exportada exitosamente", Toast.LENGTH_SHORT).show()
        }) {
            Text("Exportar Base de Datos")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón para importar la base de datos
        Button(onClick = {
            importDatabase(context)
            Toast.makeText(context, "Base de datos importada exitosamente", Toast.LENGTH_SHORT).show()
        }) {
            Text("Importar Base de Datos")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para volver
        Button(onClick = navigateBack) {
            Text("Volver")
        }
    }
}
