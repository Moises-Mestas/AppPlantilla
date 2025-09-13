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

// 📌 De la rama HEAD (Agenda, Examen, etc.)
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

// 📌 De la rama Moises (Tarea)
@Serializable
object Tarea
