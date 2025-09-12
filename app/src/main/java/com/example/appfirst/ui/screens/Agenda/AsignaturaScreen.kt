package com.example.appfirst.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.ui.asignatura.AsignaturaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignaturaScreen(viewModel: AsignaturaViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ✅ cargar el userId al entrar
    LaunchedEffect(Unit) {
        val userId = UserPrefs.getLoggedUserId(context)
        if (userId != null) {
            viewModel.setUserId(userId)
            Log.d("AsignaturaScreen", "✅ UserId cargado: $userId")
        } else {
            Log.d("AsignaturaScreen", "⚠️ No se encontró UserId en DataStore")
        }
    }

    val asignaturas by viewModel.filteredAsignaturas.collectAsState(initial = emptyList())
    val form by viewModel.form.collectAsState()
    val message by viewModel.message.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startCreate()
                    showBottomSheet = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("📚 Lista de Asignaturas", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            if (message != null) {
                Text(
                    message!!,
                    color = if (message!!.contains("exitosamente") || message!!.contains("eliminada"))
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearMessage()
                }
            }

            if (asignaturas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay asignaturas registradas")
                }
            } else {
                LazyColumn {
                    items(asignaturas) { asignatura ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(asignatura.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("👨‍🏫 ${asignatura.profesor}")
                                Text("🏫 Aula: ${asignatura.aula}")

                                Spacer(modifier = Modifier.height(8.dp))

                                Row {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.loadForEdit(asignatura.id)
                                            showBottomSheet = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        Spacer(Modifier.width(4.dp))
                                        Text("Editar")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.delete(asignatura.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                        Spacer(Modifier.width(4.dp))
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 🔹 ModalBottomSheet para formulario
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        if (viewModel.navigateToSuccess.value == null && viewModel.form.value.errors.isEmpty() && viewModel.form.value.nombre.isEmpty())
                            "Nueva Asignatura"
                        else
                            "Editar Asignatura",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.nombre,
                        onValueChange = { viewModel.onFormChange(nombre = it) },
                        label = { Text("Nombre") },
                        isError = form.errors.containsKey("nombre"),
                        modifier = Modifier.fillMaxWidth()
                    )
                    form.errors["nombre"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.profesor,
                        onValueChange = { viewModel.onFormChange(profesor = it) },
                        label = { Text("Profesor") },
                        isError = form.errors.containsKey("profesor"),
                        modifier = Modifier.fillMaxWidth()
                    )
                    form.errors["profesor"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.aula,
                        onValueChange = { viewModel.onFormChange(aula = it) },
                        label = { Text("Aula") },
                        isError = form.errors.containsKey("aula"),
                        modifier = Modifier.fillMaxWidth()
                    )
                    form.errors["aula"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.save()
                                showBottomSheet = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💾 Guardar")
                    }
                }
            }
        }
    }
}
