package com.example.appfirst.ui.screens.Agenda

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable

fun FormTareaScreen(){

    Column(){
        Row {

            Button(
                onClick = {
                },
                modifier = Modifier

                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "atras",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                },
                modifier = Modifier

                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "Guardar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row {
            Icons.Default.Add
            Text( text = "agregar titulo")
        }
        Row {
            Icons.Default.DateRange
            Text( text = "agregar fecha")
        }
        Row {
            Icons.Default.Notifications
            Text( text = "avisame a las 12")
            Text( text = "Dia anterior")
        }
        Row {
            Icons.Default.ThumbUp
            Text( text = "agregar asignatura")
        }
        Row {
            Icons.Default.AddCircle
            Text( text = "adjuntar")
        }
        Row {

            Text( text = "agregar nota")
        }

    }

}


