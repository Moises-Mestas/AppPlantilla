package com.example.appfirst.ui.screens.home

<<<<<<< HEAD
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
=======
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
>>>>>>> Moises
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.ui.user.rememberUserVM
<<<<<<< HEAD
import kotlinx.coroutines.launch
=======
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
>>>>>>> Moises
import com.example.appfirst.data.datastore.UserPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(navigateToPrincipal: () -> Unit) {
    val viewModel = rememberUserVM()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
<<<<<<< HEAD
    val context = LocalContext.current
=======
    val context = LocalContext.current 
>>>>>>> Moises

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(8.dp),
<<<<<<< HEAD
                    content = { Text(text = data.visuals.message) }
=======
                    content = {
                        Text(text = data.visuals.message)
                    }
>>>>>>> Moises
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
<<<<<<< HEAD
                                // 👇 Guardamos también el id
                                UserPrefs.setLoggedIn(
                                    context = context,
                                    isLoggedIn = true,
                                    email = user.email,
                                    userId = user.id   // <-- IMPORTANTE
                                )
                                println("✅ Login exitoso: ${user.email}, id=${user.id}")
                            }
                            navigateToPrincipal()
=======
                                UserPrefs.setLoggedIn(context, true, user.email)
                            }
                            navigateToPrincipal()
                            println("Login exitoso: ${user.email}")
>>>>>>> Moises
                        },
                        onError = { errorMessage ->
                            scope.launch {
                                snackbarHostState.showSnackbar("❌ $errorMessage")
                            }
<<<<<<< HEAD
                            println("❌ Error de login: $errorMessage")
=======
                            println("Error de login: $errorMessage")
>>>>>>> Moises
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
<<<<<<< HEAD
}
=======
}
>>>>>>> Moises
