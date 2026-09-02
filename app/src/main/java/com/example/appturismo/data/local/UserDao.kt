package com.example.appturismo.data.local

import androidx.room.*
import com.example.appturismo.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE role = 'ADMIN'")
    fun getAdmins(): Flow<List<User>>

    @Delete
    suspend fun deleteUser(user: User)
}
