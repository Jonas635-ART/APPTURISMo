package com.example.appturismo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.Recorrido
import com.example.appturismo.data.model.User
import com.example.appturismo.data.model.UserFavorite
import com.example.appturismo.util.Converters

@Database(
    entities = [PuntoTuristico::class, Recorrido::class, User::class, UserFavorite::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun puntoDao(): PuntoDao
    abstract fun recorridoDao(): RecorridoDao
    abstract fun userDao(): UserDao
    abstract fun userFavoriteDao(): UserFavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_turismo_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
