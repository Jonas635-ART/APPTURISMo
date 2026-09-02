package com.example.appturismo.data.repository

import com.example.appturismo.data.local.PuntoDao
import com.example.appturismo.data.local.UserDao
import com.example.appturismo.data.local.UserFavoriteDao
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.User
import com.example.appturismo.data.model.UserFavorite
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class TurismoRepository(
    private val puntoDao: PuntoDao,
    private val userDao: UserDao,
    private val userFavoriteDao: UserFavoriteDao
) {
    // Tourist Points
    val allPuntos: Flow<List<PuntoTuristico>> = puntoDao.getAllPuntos()

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
        // Mocking network delay
        delay(2000)
        // Here we could fetch from Retrofit and update local Room db
    }
}
