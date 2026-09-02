package com.example.appturismo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puntos_turisticos")
data class PuntoTuristico(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val fotos: List<String>, // multiple Photos (URLs)
    val latitud: Double,
    val longitud: Double,
    val esFavorito: Boolean = false // This might be local only or per user
)
