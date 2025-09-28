package com.example.appfirst.data.local.entity



enum class TipoNota {
    TRABAJO,BONOS, PROPINAS, INVERSIONES, OTROS, ALOJAMIENTO, COMIDA, ENTRETENIMIENTO, FAMILIA, MASCOTAS, EDUCACION;

    fun display(): String = when (this) {
        TRABAJO -> "TRABAJO"
        BONOS -> "BONOS"
        PROPINAS -> "PROPINAS"
        INVERSIONES -> "INVERSIONES"
        OTROS -> "OTROS..."
        ALOJAMIENTO -> "ALOJAMIENTO"
        COMIDA -> "COMIDA"
        ENTRETENIMIENTO -> "ENTRETENIMIENTO"
        FAMILIA -> "FAMILIA"
        MASCOTAS -> "MASCOTAS"
        EDUCACION -> "EDUCACIÓN"


    }
}
