package com.example.appturismo.data.local

import androidx.room.*
import com.example.appturismo.data.model.Recorrido
import kotlinx.coroutines.flow.Flow

@Dao
interface RecorridoDao {
    @Query("SELECT * FROM recorridos")
    fun getAllRecorridos(): Flow<List<Recorrido>>

    @Query("SELECT * FROM recorridos WHERE id = :id")
    suspend fun getRecorridoById(id: Int): Recorrido?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecorrido(recorrido: Recorrido): Long

    @Query("SELECT COUNT(*) FROM recorridos")
    suspend fun getCount(): Int
}
