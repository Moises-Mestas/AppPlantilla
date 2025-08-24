package com.example.appfirst

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Azul cobalto
val cobaltBlue = Color(0xFF0047AB)

@Composable
fun CuentasScreen(navController: NavController) {  // Recibimos navController como parámetro
    val showModal = remember { mutableStateOf(false) }  // Controla si se muestra la ventana emergente
    val showModal2 = remember { mutableStateOf(false) } // Controla si se muestra la segunda ventana emergente
    val showImages = remember { mutableStateOf(true) }  // Controla si las imágenes de "historial" y "mas" se deben mostrar
    val showBalance = remember { mutableStateOf(true) } // Controla si se debe mostrar el cuadro de "Balance Total"

    val topSpace = 50.dp          // Espacio arriba del título general
    val titleBottom = 8.dp        // Distancia entre "Cuentas" y el primer cuadro
    val cardsGap = 15.dp          // Separación entre Tarjeta / Efectivo / Yape
    val dividerPad = 8.dp        // Padding vertical de cada línea negra
    val imageGap = 0.dp           // Espacio entre las imágenes y la línea negra (ajustado a 0 para pegarlas)

    // Fondo cuando las ventanas emergentes están activas (sin gris)
    val backgroundModifier = if (showModal.value || showModal2.value) {
        Modifier.clickable { // Al hacer clic en cualquier parte del fondo, se cierran las ventanas emergentes
            showModal.value = false
            showModal2.value = false
            showImages.value = true
            showBalance.value = true
        }
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .then(backgroundModifier) // Aplicar el fondo cuando las ventanas emergentes están activas
    ) {
        // Espacio superior para "bajar" todo
        Spacer(Modifier.height(topSpace))

        // Parte superior (cuadros) sin fondo gris, todo blanco
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Cuentas:",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = titleBottom)
            )

            CuentaItem(
                titulo = "TARJETA:",
                detalles = listOf(
                    "1. Tarjeta 1: $/ 20.40",
                    "2. Tarjeta 2: $/ 150.60",
                    "3. Tarjeta 3: $/ 2000.90"
                )
            )

            Spacer(Modifier.height(cardsGap))

            CuentaItem(
                titulo = "EFECTIVO:",
                detalles = listOf("Dinero Efectivo: $/ 90.20")
            )

            Spacer(Modifier.height(cardsGap))

            CuentaItem(
                titulo = "YAPE:",
                detalles = listOf("Yape: $/ 122.30")
            )
        }

        // Espacio para agregar las imágenes antes de la primera línea negra (solo si el estado showImages es verdadero)
        if (showImages.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = imageGap),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.historial),
                    contentDescription = "Imagen 1",
                    modifier = Modifier
                        .size(70.dp)
                        .offset(y = -5.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.mas),
                    contentDescription = "Imagen 2",
                    modifier = Modifier
                        .size(55.dp)
                        .offset(y = 5.dp)
                        .clickable() {
                            showModal.value = true // Mostrar la primera ventana emergente
                            showModal2.value = true // Mostrar la segunda ventana emergente
                            showImages.value = false // Ocultar las imágenes de "historial" y "mas"
                            showBalance.value = false // Ocultar el cuadro de Balance Total
                        }
                )
            }
        }

        // Línea negra 1
        Divider(
            color = Color.Black,
            thickness = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dividerPad)
        )

        // Si showModal es verdadero, mostramos la ventana emergente 1
        if (showModal.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Ventana emergente 1 con fondo gris
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)) // Fondo gris claro
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("gasto") // Redirige a GastoScreen al hacer click
                        }
                ) {
                    Text(
                        text = "GASTO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp // Aumento tamaño de título
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.gasto),
                            contentDescription = "Imagen 1",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registra una compra o un pago/gasto que hiciste en tu día.",
                            fontSize = 18.sp, // Aumento tamaño de texto
                            modifier = Modifier.weight(1f) // El texto se coloca a la derecha de la imagen
                        )
                    }
                }
            }
        }

        // Si showModal2 es verdadero, mostramos la ventana emergente 2
        if (showModal2.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Ventana emergente 2 con fondo gris
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)) // Fondo gris claro
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("ingreso") // Redirige a IngresoScreen al hacer click
                        }
                ) {
                    Text(
                        text = "INGRESO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp // Aumento tamaño de título
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.ingreso),
                            contentDescription = "Imagen 2",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registra tu salario, bonos o algún ingreso obtenido en tu día.",
                            fontSize = 18.sp, // Aumento tamaño de texto
                            modifier = Modifier.weight(1f) // El texto se coloca a la derecha de la imagen
                        )
                    }
                }
            }
        }

        // Balance Total (si showBalance es verdadero)
        if (showBalance.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(2.dp, cobaltBlue, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "BALANCE TOTAL:",
                    color = cobaltBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$/ 2384.40",
                    color = cobaltBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Línea negra 2
        Divider(
            color = Color.Black,
            thickness = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dividerPad)
        )

        // 5 imágenes en la tercera parte
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(painter = painterResource(id = R.drawable.agenda), contentDescription = "Imagen 1", modifier = Modifier.size(45.dp))
            Image(painter = painterResource(id = R.drawable.dinero2), contentDescription = "Imagen 2", modifier = Modifier.size(45.dp))
            Image(painter = painterResource(id = R.drawable.perfil), contentDescription = "Imagen 3", modifier = Modifier.size(50.dp))
            Image(painter = painterResource(id = R.drawable.calendario), contentDescription = "Imagen 4", modifier = Modifier.size(50.dp))
            Image(painter = painterResource(id = R.drawable.historial), contentDescription = "Imagen 5", modifier = Modifier.size(50.dp))
        }
    }
}

@Composable
fun CuentaItem(
    titulo: String,
    detalles: List<String>
) {
    val itemOuterVertical = 8.dp
    val bluePad = 10.dp
    val whitePad = 30.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = itemOuterVertical)
    ) {
        Text(
            text = titulo,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .background(cobaltBlue, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(bluePad)
        )

        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(2.dp, cobaltBlue, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(whitePad)
        ) {
            detalles.forEach { detalle ->
                val parts = detalle.split(":")
                if (parts.size == 2) {
                    val texto = parts[0].trim()
                    val precio = parts[1].trim()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = texto,
                            color = Color.Black,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = precio,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                } else {
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
