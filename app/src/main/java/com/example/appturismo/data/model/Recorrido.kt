package com.example.appturismo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recorridos")
data class Recorrido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val region: String,
    val duracionEstimada: String,
    val distanciaTotal: String,
    val urlImagen: String
)
