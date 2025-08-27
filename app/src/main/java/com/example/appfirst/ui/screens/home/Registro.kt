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
import com.example.appfirst.ui.user.rememberUserVM // Importar el ViewModel

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Crear Cuenta",
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = formState.name, // ← Valor del ViewModel
            onValueChange = { viewModel.onFormChange(name = it) }, // ← Actualizar ViewModel
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("name") // ← Mostrar error
        )
        if (formState.errors.containsKey("name")) {
            Text(
                text = formState.errors["name"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.lastname,
            onValueChange = { viewModel.onFormChange(lastname = it) },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("lastname")
        )
        if (formState.errors.containsKey("lastname")) {
            Text(
                text = formState.errors["lastname"] ?: "",
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.age,
            onValueChange = { viewModel.onFormChange(age = it) },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("age")
        )
        if (formState.errors.containsKey("age")) {
            Text(
                text = formState.errors["age"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.phone,
            onValueChange = { viewModel.onFormChange(phone = it) },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.errors.containsKey("phone")
        )
        if (formState.errors.containsKey("phone")) {
            Text(
                text = formState.errors["phone"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navigateToHome()
                viewModel.save()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Registrarse",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}