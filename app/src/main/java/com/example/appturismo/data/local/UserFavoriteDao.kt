package com.example.appturismo.data.local

import androidx.room.*
import com.example.appturismo.data.model.UserFavorite

@Dao
interface UserFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: UserFavorite)

    @Delete
    suspend fun removeFavorite(favorite: UserFavorite)

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE userId = :userId AND puntoId = :puntoId)")
    suspend fun isFavorite(userId: Int, puntoId: Int): Boolean
}
