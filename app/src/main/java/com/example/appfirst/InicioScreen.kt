package com.example.appfirst

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun InicioScreen(navController: NavController) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Título de la pantalla
        Text(
            text = "Iniciar Sesión",
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Imagen clickeable
        Image(
            painter = painterResource(id = R.drawable.mas),  // Reemplaza con tu imagen en drawable
            contentDescription = "Imagen de inicio",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 32.dp)
                .clickable {
                    // Navegar a la pantalla Monedero
                    navController.navigate("monedero")
                },
            contentScale = ContentScale.Crop
        )


        // Campos de texto
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

        // Botón de acceso
        Button(
            onClick = {
                println("Email: ${emailState.value}")
                println("Password: ${passwordState.value}")
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
