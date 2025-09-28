package com.example.appfirst.data.local.entity

enum class MedioPago {
    TARJETA, YAPE, EFECTIVO;

    fun display(): String = when (this) {
        TARJETA -> "Tarjeta"
        YAPE -> "Yape"
        EFECTIVO -> "Efectivo"
    }
}
