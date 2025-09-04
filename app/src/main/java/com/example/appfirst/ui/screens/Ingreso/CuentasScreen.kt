package com.example.appfirst.ui.screens.ingreso

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.appfirst.ui.screens.home.NavDestination
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    navigateBack: () -> Unit // Función para navegar atrás
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    var open by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text("Vista Cuentas", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                val navItems = listOf(
                    NavDestination.DATE,
                    NavDestination.SONGS,
                    NavDestination.HOME,
                    NavDestination.FAVORITES,
                    NavDestination.PROFILE
                )
                navItems.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Título "Cuentas"
            Text(
                text = "Cuentas:",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            // Sección TARJETA (morada)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "TARJETA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Cuadro gris TARJETA (botón)
            Button(
                onClick = { /* Acción para tarjeta */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Detalles de tarjeta...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth() // texto alineado a la izquierda
                )
            }

            // Sección EFECTIVO (morada)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "EFECTIVO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Cuadro gris EFECTIVO (botón)
            Button(
                onClick = { /* Acción efectivo */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Detalles de efectivo...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Sección YAPE (morada)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "YAPE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Cuadro gris YAPE (botón)
            Button(
                onClick = { /* Acción yape */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Detalles de yape...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 👇 FAB + ventana emergente
        AddFabWithSheet(
            sheetOffsetY = -30.dp, // Ajustado a 50.dp para mover los botones más arriba
            bottomPadding = innerPadding.calculateBottomPadding(),
            open = open,
            onOpenChange = { open = it }
        )
    }
}


@Composable
fun AddFabWithSheet(
    sheetOffsetY: Dp = 50.dp,   // Mueve la ventana emergente más arriba ajustando este valor
    bottomPadding: Dp = 0.dp,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit
) {
    Box(Modifier.fillMaxSize()) {

        // FAB (botón +)
        FloatingActionButton(
            onClick = { onOpenChange(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp + bottomPadding), // Ajuste del FAB
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
        }

        if (open) {
            // Fondo oscuro
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                    .clickable { onOpenChange(false) } // Cerrar al hacer clic en el fondo oscuro
            )

            // Solo los botones (sin fondo)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp + bottomPadding)
                    .offset(y = sheetOffsetY),  // Ajuste de la posición de los botones
                verticalArrangement = Arrangement.spacedBy(20.dp) // espacio entre botones
            ) {
                // BOTÓN GASTO
                ElevatedButton(
                    onClick = { /* acción gasto */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp), // más pequeño
                    shape = RectangleShape, // cuadrado
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp) // icono mediano
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Gasto", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Registra una compra o un pago/gasto que hiciste en tu día.",
                            fontSize = 16.sp, // más grande que antes
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // BOTÓN INGRESO
                ElevatedButton(
                    onClick = { /* acción ingreso */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Ingreso", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Registra tu salario, bonos o algún ingreso obtenido en tu día.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}




