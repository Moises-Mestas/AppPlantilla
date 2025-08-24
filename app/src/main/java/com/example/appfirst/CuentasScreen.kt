package com.example.appfirst

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Azul cobalto
val cobaltBlue = Color(0xFF0047AB)

@Composable
fun CuentasScreen() {
    val topSpace = 50.dp          // <- controla: espacio arriba del título general
    val titleBottom = 8.dp        // <- controla: distancia entre "Cuentas" y el primer cuadro
    val cardsGap = 15.dp           // <- controla: separación entre Tarjeta / Efectivo / Yape
    val dividerPad = 12.dp        // <- controla: padding vertical de cada línea negra


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Espacio superior para "bajar" todo
        Spacer(Modifier.height(topSpace)) // <- controla

        // Parte superior (cuadros)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Cuentas:",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = titleBottom) // <- controla
            )

            CuentaItem(
                titulo = "TARJETA:",
                detalles = listOf(
                    "1. Nombre tarjeta 1: $/ 20.40",
                    "2. Nombre tarjeta 2: $/ 150.60",
                    "3. Nombre tarjeta 3: $/ 2000.90"
                )
            )

            Spacer(Modifier.height(cardsGap)) // <- controla

            CuentaItem(
                titulo = "EFECTIVO:",
                detalles = listOf("Dinero Efectivo: $/ 90.20")
            )

            Spacer(Modifier.height(cardsGap)) // <- controla

            CuentaItem(
                titulo = "YAPE:",
                detalles = listOf("Yape: $/ 122.30")
            )
        }

        // Dos imágenes antes de la primera línea
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.historial),
                contentDescription = "Imagen 1",
                modifier = Modifier.size(70.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.mas),
                contentDescription = "Imagen 2",
                modifier = Modifier.size(55.dp)
            )
        }

        // Línea negra 1
        Divider(
            color = Color.Black,
            thickness = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dividerPad) // <- controla
        )

        // Balance Total
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

        // Línea negra 2
        Divider(
            color = Color.Black,
            thickness = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dividerPad) // <- controla
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
    // ======= CONTROLES DE ESPACIADO DEL ITEM =======
    val itemOuterVertical = 8.dp  // <- controla: margen vertical exterior del item
    val bluePad = 10.dp           // <- controla: padding interno del rectángulo azul
    val whitePad = 30.dp          // <- controla: padding interno del rectángulo blanco
    // ================================================

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = itemOuterVertical) // <- controla
    ) {
        // Cabecera azul
        Text(
            text = titulo,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .background(cobaltBlue, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(bluePad) // <- controla
        )

        // Contenido blanco
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(2.dp, cobaltBlue, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(whitePad) // <- controla
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
                            fontSize = 17.sp,
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
