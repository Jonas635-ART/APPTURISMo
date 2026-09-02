package com.example.appturismo

import android.app.Application
import com.example.appturismo.data.local.AppDatabase
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TurismoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { 
        TurismoRepository(database.puntoDao(), database.userDao(), database.userFavoriteDao()) 
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize default admin if needed
        CoroutineScope(Dispatchers.IO).launch {
            val admin = repository.getUserByUsername("admin")
            if (admin == null) {
                repository.register(com.example.appturismo.data.model.User(username = "admin", password = "admin", role = "ADMIN"))
            }
        }
    }
}
