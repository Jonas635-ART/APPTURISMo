package com.example.appturismo.data.model

import androidx.room.Entity

@Entity(tableName = "user_favorites", primaryKeys = ["userId", "puntoId"])
data class UserFavorite(
    val userId: Int,
    val puntoId: Int
)
