package com.example.appfirst.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirst.ui.user.rememberUserVM

@Composable
fun RegistroScreen(navigateToHome: () -> Unit) {
    val viewModel = rememberUserVM()
    val formState by viewModel.form.collectAsState()
    val navigateId by viewModel.navigateToSuccess.collectAsState()

    if (navigateId != null) {
        LaunchedEffect(navigateId) {
            navigateToHome()
            viewModel.resetNavigation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp), // Padding en la columna
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp)) // Ajusta el espacio arriba del título

        Text(
            text = "Crear Cuenta",
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = formState.name,
            onValueChange = { viewModel.onFormChange(name = it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("name")
        )
        if (formState.errors.containsKey("name")) {
            Text(
                text = formState.errors["name"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.email,
            onValueChange = { viewModel.onFormChange(email = it) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("email")
        )
        if (formState.errors.containsKey("email")) {
            Text(
                text = formState.errors["email"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.password,
            onValueChange = { viewModel.onFormChange(password = it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("password")
        )
        if (formState.errors.containsKey("password")) {
            Text(
                text = formState.errors["password"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.confirmPassword,
            onValueChange = { viewModel.onFormChange(confirmPassword = it) },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("confirmPassword")
        )
        if (formState.errors.containsKey("confirmPassword")) {
            Text(
                text = formState.errors["confirmPassword"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón con padding hacia abajo
        Button(
            onClick = {
                viewModel.save()
                navigateToHome()  // Llamada a la navegación correcta
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Registrarse",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

