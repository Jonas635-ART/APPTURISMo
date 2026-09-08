package com.example.appturismo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puntos_turisticos")
data class PuntoTuristico(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recorridoId: Int = 1,
    val orden: Int = 1, // 1 al 5
    val nombre: String,
    val descripcion: String,
    val fotos: List<String>,
    val latitud: Double,
    val longitud: Double,
    val esFavorito: Boolean = false
)
