package com.example.appfirst

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Define el color azul cobalto
val cobaltBlue = Color(0xFF0047AB)

@Composable
fun CuentasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))  // Ajusta el valor aquí según el espacio que necesites

        // Parte superior (Cuadros con detalles)
        Column(
            modifier = Modifier.weight(1f)  // Ocupa la parte superior
        ) {
            // Título de la pantalla
            Text(
                text = "Cuentas:",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // Cuadro azul cobalto con texto para "Tarjeta"
            CuentaItem(titulo = "TARJETA:", detalles = listOf("1. Nombre tarjeta 1: $/ 20.40", "2. Nombre tarjeta 2: $/ 150.60", "3. Nombre tarjeta 3: $/ 2000.90"))

            // Espaciado entre cuadros
            Spacer(modifier = Modifier.height(16.dp))

            // Cuadro azul cobalto con texto para "Efectivo"
            CuentaItem(titulo = "EFECTIVO:", detalles = listOf("Dinero Efectivo: $/ 90.20"))

            // Espaciado entre cuadros
            Spacer(modifier = Modifier.height(16.dp))

            // Cuadro azul cobalto con texto para "Yape"
            CuentaItem(titulo = "YAPE:", detalles = listOf("Yape: $/ 122.30"))
        }

        // Línea negra de separación entre la parte superior e inferior
        Divider(
            color = Color.Black,
            thickness = 6.dp, // Grosor ajustado de la línea
            modifier = Modifier
                .fillMaxWidth() // Línea que ocupa todo el ancho de la pantalla
                .padding(vertical = 16.dp) // Espacio alrededor de la línea
        )

        // Parte intermedia (Línea negra y Balance Total)
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cuadro blanco para el balance total
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(2.dp, cobaltBlue, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                // Texto "BALANCE TOTAL:" con un tamaño de fuente más pequeño
                Text(
                    text = "BALANCE TOTAL:",
                    color = cobaltBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,  // Reducción del tamaño de la fuente
                    modifier = Modifier.weight(1f)
                )

                // Precio "$/ 2384.40" en azul, del mismo tamaño
                Text(
                    text = "$/ 2384.40",
                    color = cobaltBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,  // Igual tamaño que el de "BALANCE TOTAL"
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Segunda línea negra de separación debajo del cuadro de "BALANCE TOTAL"
        Divider(
            color = Color.Black,
            thickness = 6.dp, // Grosor de la segunda línea
            modifier = Modifier
                .fillMaxWidth() // Línea que ocupa todo el ancho de la pantalla
                .padding(vertical = 16.dp) // Espacio alrededor de la línea
        )



        // Agregar las imágenes en una fila horizontal (Row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween  // Para espaciar las imágenes uniformemente
        ) {
            Image(
                painter = painterResource(id = R.drawable.agenda),  // Reemplaza con tu imagen
                contentDescription = "Imagen 1",
                modifier = Modifier.size(45.dp)  // Tamaño de la imagen
            )
            Image(
                painter = painterResource(id = R.drawable.dinero2),  // Reemplaza con tu imagen
                contentDescription = "Imagen 2",
                modifier = Modifier.size(45.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.perfil),  // Reemplaza con tu imagen
                contentDescription = "Imagen 3",
                modifier = Modifier.size(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.calendario),  // Reemplaza con tu imagen
                contentDescription = "Imagen 4",
                modifier = Modifier.size(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.historial),  // Reemplaza con tu imagen
                contentDescription = "Imagen 5",
                modifier = Modifier.size(50.dp)
            )
        }


    }
}

@Composable
fun CuentaItem(titulo: String, detalles: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Cuadro azul cobalto para el título
        Text(
            text = titulo,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .background(cobaltBlue, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(12.dp)
        )

        // Cuadro blanco para los detalles con texto negro y borde azul cobalto
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(2.dp, cobaltBlue, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            detalles.forEach { detalle ->
                // Separar el texto y el precio en dos elementos de texto
                val parts = detalle.split(":")
                if (parts.size == 2) {
                    // Aquí separamos texto y precio
                    val texto = parts[0].trim()
                    val precio = parts[1].trim()

                    // Alineamos el texto a la izquierda y el precio a la derecha
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = texto,
                            color = Color.Black,
                            fontSize = 17.sp,
                            modifier = Modifier.weight(1f)  // Alinea el texto a la izquierda
                        )
                        Text(
                            text = precio,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,  // Poner en negrita el precio
                            fontSize = 17.sp,
                            modifier = Modifier.padding(start = 8.dp)  // Mover el precio un poco hacia la derecha
                        )
                    }
                } else {
                    // Mostrar el detalle sin separar texto y precio (si no tiene ":")
                    Text(
                        text = detalle,
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
