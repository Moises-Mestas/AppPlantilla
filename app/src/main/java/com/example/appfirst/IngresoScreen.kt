package com.example.appfirst

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun IngresoScreen(navController: NavController) {
    val showModal = remember { mutableStateOf(false) }   // GASTO
    val showModal2 = remember { mutableStateOf(false) }  // INGRESO
    val showImages = remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf("TARJETA") }

    var bottomBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    // Controles ajustables
    var masOffsetFromBar by remember { mutableStateOf(6.dp) }
    var masEndPad       by remember { mutableStateOf(16.dp) }
    var iconsBottomPad  by remember { mutableStateOf(18.dp) }
    var cardsOffsetFromBar by remember { mutableStateOf(8.dp) }
    var saveOffsetFromBar by remember { mutableStateOf(8.dp) }


    var flechaSize by remember { mutableStateOf(44.dp) }     // tamaño de la imagen
    var flechaStartPad by remember { mutableStateOf(10.dp) }  // empuje hacia adentro desde el borde izquierdo
    var flechaYOffset by remember { mutableStateOf(2.dp) }   // subir/bajar la flecha (negativo = arriba)
    Box(Modifier.fillMaxSize()) {

        // =================== CONTENIDO (scroll) ===================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = bottomBarHeight + 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(45.dp))

            // ← TÍTULO con flecha a la izquierda
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.flecha),
                    contentDescription = "Volver",
                    modifier = Modifier
                        .align(Alignment.CenterStart)     // queda a la izquierda del título
                        .padding(start = flechaStartPad)  // mueve a la derecha desde el borde
                        .offset(y = flechaYOffset)        // sube/baja sin afectar el layout del título
                        .size(flechaSize)                 // agranda/achica la flecha
                        .clickable { navController.navigate("cuentas") }
                )


                Text(
                    text = "Agregar Ingreso",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Monto",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(-10.dp))

            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Ingrese el monto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFF56C8F7), shape = RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp)),
                textStyle = TextStyle(color = Color.Black)
            )

            Spacer(Modifier.height(10.dp))

            Divider(
                color = Color.Gray, thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            )

            Text("Descripción:", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)

            Spacer(Modifier.height(5.dp))

            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.historial),
                    contentDescription = "Imagen Descripción",
                    modifier = Modifier
                        .size(65.dp)
                        .padding(end = 1.dp)
                        .offset(x = 4.dp, y = 10.dp)
                )

                TextField(
                    value = "", onValueChange = {},
                    label = { Text("Escribe la descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp)),
                    textStyle = TextStyle(color = Color.Black)
                )
            }

            Divider(
                color = Color.Gray, thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            )

            Text("Cuenta:", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)

            Spacer(Modifier.height(5.dp))

            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = { selectedOption = "TARJETA" },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            if (selectedOption == "TARJETA") Color(0xFF808080) else Color.Transparent,
                            shape = RoundedCornerShape(2.dp)
                        )
                ) { Text("TARJETA", color = Color.White) }

                Button(
                    onClick = { selectedOption = "EFECTIVO" },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            if (selectedOption == "EFECTIVO") Color(0xFF808080) else Color.Transparent,
                            shape = RoundedCornerShape(2.dp)
                        )
                ) { Text("EFECTIVO", color = Color.White) }

                Button(
                    onClick = { selectedOption = "YAPE" },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            if (selectedOption == "YAPE") Color(0xFF808080) else Color.Transparent,
                            shape = RoundedCornerShape(2.dp)
                        )
                ) { Text("YAPE", color = Color.White) }
            }

            Divider(
                color = Color.Gray, thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            )

            Text("Fecha Realizada:", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)

            Spacer(Modifier.height(5.dp))

            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.calendario),
                    contentDescription = "Imagen Calendario",
                    modifier = Modifier
                        .size(65.dp)
                        .padding(end = 10.dp)
                        .offset(x = 10.dp, y = 10.dp)
                )

                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Selecciona la fecha") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp)),
                    textStyle = TextStyle(color = Color.Black)
                )
            }

            Divider(
                color = Color.Gray, thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            )

            Text("Notas:", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)

            Spacer(Modifier.height(5.dp))

            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.mensaje),
                    contentDescription = "Imagen Mensaje",
                    modifier = Modifier
                        .size(65.dp)
                        .padding(end = 10.dp)
                        .offset(x = 10.dp, y = 10.dp)
                )

                TextField(
                    value = "", onValueChange = {},
                    label = { Text("Escribe una nota") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Gray, shape = RoundedCornerShape(12.dp)),
                    textStyle = TextStyle(color = Color.Black)
                )
            }
        }

        // ===== Botón “GUARDAR” flotante =====
        if (!showModal.value && !showModal2.value) {
            Button(
                onClick = { /* TODO: lógica de guardado */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = bottomBarHeight + saveOffsetFromBar
                    )
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .zIndex(6f)
            ) {
                Text("GUARDAR", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ===== Botón “MÁS” flotante =====
        if (showImages.value) {
            Image(
                painter = painterResource(id = R.drawable.mas),
                contentDescription = "Más",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = masEndPad, bottom = bottomBarHeight + masOffsetFromBar)
                    .size(55.dp)
                    .zIndex(6f)
                    .clickable {
                        showModal.value = true
                        showModal2.value = true
                        showImages.value = false
                    }
            )
        }

        // =================== OVERLAY (gris: cubre TODO) ===================
        if (showModal.value || showModal2.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .zIndex(4f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        showModal.value = false
                        showModal2.value = false
                        showImages.value = true
                    }
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = bottomBarHeight + cardsOffsetFromBar)
                    .zIndex(5f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .clickable { navController.navigate("gasto") }
                ) {
                    Text("GASTO", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Row(Modifier.fillMaxWidth()) {
                        Image(painter = painterResource(id = R.drawable.gasto), contentDescription = "Gasto", modifier = Modifier.size(50.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Registra una compra o un pago/gasto que hiciste en tu día.", fontSize = 18.sp, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .clickable { navController.navigate("ingreso") }
                ) {
                    Text("INGRESO", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Row(Modifier.fillMaxWidth()) {
                        Image(painter = painterResource(id = R.drawable.ingreso), contentDescription = "Ingreso", modifier = Modifier.size(50.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Registra tu salario, bonos o algún ingreso obtenido en tu día.", fontSize = 18.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // =================== BARRA INFERIOR FIJA ===================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(3f)
                .onGloballyPositioned { coords ->
                    bottomBarHeight = with(density) { coords.size.height.toDp() }
                }
        ) {
            Column(Modifier.fillMaxWidth()) {
                Divider(
                    color = Color.Black, thickness = 6.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = iconsBottomPad),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(painter = painterResource(id = R.drawable.agenda), contentDescription = "Agenda", modifier = Modifier.size(45.dp))
                    Image(painter = painterResource(id = R.drawable.dinero2), contentDescription = "Dinero", modifier = Modifier.size(45.dp))
                    Image(painter = painterResource(id = R.drawable.perfil), contentDescription = "Perfil", modifier = Modifier.size(50.dp))
                    Image(painter = painterResource(id = R.drawable.calendario), contentDescription = "Calendario", modifier = Modifier.size(50.dp))
                    Image(painter = painterResource(id = R.drawable.historial), contentDescription = "Historial", modifier = Modifier.size(50.dp))
                }
            }
        }
    }
}
