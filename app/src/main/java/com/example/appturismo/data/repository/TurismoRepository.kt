package com.example.appturismo.data.repository

import com.example.appturismo.data.local.PuntoDao
import com.example.appturismo.data.local.RecorridoDao
import com.example.appturismo.data.local.UserDao
import com.example.appturismo.data.local.UserFavoriteDao
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.Recorrido
import com.example.appturismo.data.model.User
import com.example.appturismo.data.model.UserFavorite
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class TurismoRepository(
    private val puntoDao: PuntoDao,
    private val recorridoDao: RecorridoDao,
    private val userDao: UserDao,
    private val userFavoriteDao: UserFavoriteDao
) {
    // Recorridos
    val allRecorridos: Flow<List<Recorrido>> = recorridoDao.getAllRecorridos()
    suspend fun getRecorridoById(id: Int) = recorridoDao.getRecorridoById(id)
    suspend fun insertRecorrido(recorrido: Recorrido) = recorridoDao.insertRecorrido(recorrido)
    suspend fun getRecorridosCount() = recorridoDao.getCount()

    // Tourist Points
    val allPuntos: Flow<List<PuntoTuristico>> = puntoDao.getAllPuntos()
    fun getPuntosByRecorrido(recorridoId: Int): Flow<List<PuntoTuristico>> = puntoDao.getPuntosByRecorrido(recorridoId)
    suspend fun getPuntosCount() = puntoDao.getCount()

    suspend fun insertPunto(punto: PuntoTuristico) = puntoDao.insertPunto(punto)
    suspend fun updatePunto(punto: PuntoTuristico) = puntoDao.updatePunto(punto)
    suspend fun deletePunto(punto: PuntoTuristico) = puntoDao.deletePunto(punto)
    suspend fun getPuntoById(id: Int) = puntoDao.getPuntoById(id)

    // Users
    suspend fun login(username: String, password: String) = userDao.login(username, password)
    suspend fun register(user: User) = userDao.insertUser(user)
    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)
    fun getAdmins() = userDao.getAdmins()

    // Favorites
    fun getFavorites(userId: Int) = puntoDao.getFavoritePuntos(userId)
    suspend fun addFavorite(userId: Int, puntoId: Int) {
        userFavoriteDao.addFavorite(UserFavorite(userId, puntoId))
    }
    suspend fun removeFavorite(userId: Int, puntoId: Int) {
        userFavoriteDao.removeFavorite(UserFavorite(userId, puntoId))
    }
    suspend fun isFavorite(userId: Int, puntoId: Int) = userFavoriteDao.isFavorite(userId, puntoId)

    // Sync mechanism (Mocking Retrofit)
    suspend fun syncData() {
        delay(1500)
    }
}
