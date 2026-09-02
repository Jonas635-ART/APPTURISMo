package com.example.appturismo.data.local

import androidx.room.*
import com.example.appturismo.data.model.PuntoTuristico
import kotlinx.coroutines.flow.Flow

@Dao
interface PuntoDao {
    @Query("SELECT * FROM puntos_turisticos")
    fun getAllPuntos(): Flow<List<PuntoTuristico>>

    @Query("SELECT * FROM puntos_turisticos WHERE id = :id")
    suspend fun getPuntoById(id: Int): PuntoTuristico?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPunto(punto: PuntoTuristico)

    @Update
    suspend fun updatePunto(punto: PuntoTuristico)

    @Delete
    suspend fun deletePunto(punto: PuntoTuristico)

    @Query("SELECT * FROM puntos_turisticos WHERE id IN (SELECT puntoId FROM user_favorites WHERE userId = :userId)")
    fun getFavoritePuntos(userId: Int): Flow<List<PuntoTuristico>>
}
