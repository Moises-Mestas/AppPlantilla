package com.example.appfirst.core.navigation

import kotlinx.serialization.Serializable


@Serializable
object Login

@Serializable
object Inicio

@Serializable
object Registro

@Serializable
object Principal

@Serializable
object Agenda

@Serializable
object FormTarea

@Serializable
object Asignatura

@Serializable
object Examen

@Serializable
object  Recordatorio

//seccion del calendario :v

@Serializable object CalendarioScreen
@Serializable data class DetallesFecha(val fecha: String)
@Serializable data class FormularioNota(val fecha: String)
@Serializable data class EditarNota(val notaId: Int)
@Serializable object HorarioDiario
@Serializable data class EditarAccion(val accionId: Int)
@Serializable object NuevaAccion
@Serializable data class FormularioAccion(val accionId: Int = 0)