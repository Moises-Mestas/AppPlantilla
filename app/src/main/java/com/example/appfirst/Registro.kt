package com.example.appfirst

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistroScreen() {
    // Estados para cada campo del formulario
    val nombreState = remember { mutableStateOf("") }
    val apellidoState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val edadState = remember { mutableStateOf("") }
    val telefonoState = remember { mutableStateOf("") }

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

        // Campos sin keyboardOptions (versión simple)
        OutlinedTextField(
            value = nombreState.value,
            onValueChange = { nombreState.value = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = apellidoState.value,
            onValueChange = { apellidoState.value = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = edadState.value,
            onValueChange = { edadState.value = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = telefonoState.value,
            onValueChange = { telefonoState.value = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                println("Nombre: ${nombreState.value}")
                println("Apellido: ${apellidoState.value}")
                println("Email: ${emailState.value}")
                println("Password: ${passwordState.value}")
                println("Edad: ${edadState.value}")
                println("Teléfono: ${telefonoState.value}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text(text = "Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}