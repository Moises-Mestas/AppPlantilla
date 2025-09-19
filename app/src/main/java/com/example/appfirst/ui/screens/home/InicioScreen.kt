package com.example.appfirst.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.ui.user.rememberUserVM
import kotlinx.coroutines.launch
import com.example.appfirst.data.datastore.UserPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(navigateToPrincipal: () -> Unit) {
    val viewModel = rememberUserVM()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    content = { Text(text = data.visuals.message) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Iniciar Sesión",
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (emailState.value.isBlank() || passwordState.value.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("❌ Completa todos los campos")
                        }
                        return@Button
                    }

                    viewModel.login(
                        email = emailState.value,
                        password = passwordState.value,
                        onSuccess = { user ->
                            scope.launch {
                                UserPrefs.setLoggedIn(
                                    context = context,
                                    isLoggedIn = true,
                                    email = user.email,
                                    userId = user.id
                                )
                                println("✅ Login exitoso: ${user.email}, id=${user.id}")
                            }
                            navigateToPrincipal()
                        },
                        onError = { errorMessage ->
                            scope.launch {
                                snackbarHostState.showSnackbar("❌ $errorMessage")
                            }
                            println("❌ Error de login: $errorMessage")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(
                    text = "Acceder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}