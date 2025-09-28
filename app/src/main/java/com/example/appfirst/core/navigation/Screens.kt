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

// ----------------------
// 📌 De la rama HEAD
// ----------------------
@Serializable
object Agenda

@Serializable
object FormTarea

@Serializable
object Asignatura

@Serializable
object Examen

@Serializable
object Recordatorio

// Sección del calendario
@Serializable
object VistaCalendario

@Serializable
object DetallesFecha

@Serializable
object VistaNotas

@Serializable
object Tarea

// ----------------------
// 📌 De la rama Nelson
// ----------------------
@Serializable
object CalendarioScreen

@Serializable
data class DetallesFechaNelson(val fecha: String)

@Serializable
data class FormularioNota(val fecha: String)

@Serializable
data class EditarNota(val notaId: Int)

@Serializable
object HorarioDiario

@Serializable
data class EditarAccion(val accionId: Int)

@Serializable
object NuevaAccion

@Serializable
data class FormularioAccion(val accionId: Int = 0)
