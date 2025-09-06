// com.example.appfirst.ui.ingresos.HistorialScreen.kt
package com.example.appfirst.ui.ingresos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.ui.ingreso.rememberIngresoVM
import com.example.appfirst.ui.screens.home.NavDestination
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navigateToCuentas: () -> Unit,
    navigateToFormIngreso2: () -> Unit,  // Para navegar al formulario de ingreso
    navigateToFormGasto: () -> Unit,    // Para navegar al formulario de gasto


    navigateBack: () -> Unit
) {
    val viewModel = rememberIngresoVM()
    val context = LocalContext.current
    val ingresos by viewModel.ingresos.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Carga userId
    LaunchedEffect(Unit) {
        try {
            val userDao = AppDatabase.get(context).userDao()
            val userId = withContext(Dispatchers.IO) {
                val userEmail = UserPrefs.getLoggedUserEmail(context)
                val users = userDao.getAllUsers().first()
                users.firstOrNull { it.email == userEmail }?.id
            }
            if (userId != null) viewModel.setUserId(userId) else errorMessage = "Usuario no encontrado"
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Ingresos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                NavDestination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (index == 3) navigateToCuentas() // Navegar a cuentas
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.contentDescription) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Button(
                onClick = navigateToFormIngreso2,  // Cambié el nombre aquí
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Agregar ingreso")
            }

            Button(
                onClick = navigateToFormGasto,  // Cambié el nombre aquí
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Agregar gasto")
            }

            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
                ingresos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ingresos. ¡Agrega uno desde el botón!")
                }
                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingresos) { ingreso ->
                        IngresoItemSimple(ingreso = ingreso)
                    }
                }
            }
        }
    }
}


@Composable
fun IngresoItemSimple(ingreso: Ingreso) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ingreso.descripcion,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Monto: S/ ${"%.2f".format(ingreso.monto)}", fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            Text("Depositado en: ${ingreso.depositadoEn}", fontSize = 12.sp)
            if (ingreso.notas.isNotBlank()) {
                Text("Notas: ${ingreso.notas}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Text("Fecha: ${formatFecha(ingreso.fecha)}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// 👇 Deja este helper aquí para que lo use la lista (y también la vista 2 lo verá por estar en el mismo package)
fun formatFecha(timestamp: Long): String = try {
    android.text.format.DateFormat.format("dd/MM/yy HH:mm", Date(timestamp)).toString()
} catch (e: Exception) {
    "Fecha inválida"
}
