package com.example.appturismo

import android.app.Application
import com.example.appturismo.data.local.AppDatabase
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.Recorrido
import com.example.appturismo.data.model.User
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TurismoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { 
        TurismoRepository(
            database.puntoDao(),
            database.recorridoDao(),
            database.userDao(),
            database.userFavoriteDao()
        ) 
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Default Admin User
            val admin = repository.getUserByUsername("admin")
            if (admin == null) {
                repository.register(User(username = "admin", password = "admin", role = "ADMIN"))
            }

            // 2. Pre-populate Default Recorridos if empty
            if (repository.getRecorridosCount() == 0) {
                val rec1Id = repository.insertRecorrido(
                    Recorrido(
                        nombre = "Recorrido 1: Centro Histórico Emblemático",
                        descripcion = "Recorrido guiado de 5 puntos por las joyas arquitectónicas e históricas.",
                        region = "Sierra / Centro",
                        duracionEstimada = "3 Horas",
                        distanciaTotal = "6.5 km",
                        urlImagen = "https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?w=800"
                    )
                ).toInt()

                val rec2Id = repository.insertRecorrido(
                    Recorrido(
                        nombre = "Recorrido 2: Ruta del Malecón y Tradición",
                        descripcion = "Recorrido costero guiado con 5 paradas emblemáticas llenas de cultura y río.",
                        region = "Costa / Guayas",
                        duracionEstimada = "4 Horas",
                        distanciaTotal = "18 km",
                        urlImagen = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"
                    )
                ).toInt()

                // Insert 5 Puntos for Recorrido 1
                repository.insertPunto(PuntoTuristico(recorridoId = rec1Id, orden = 1, nombre = "Punto 1: Plaza Grande y Palacio Municipal", descripcion = "Corazón político e histórico con jardines tradicionales y arquitectura colonial.", fotos = listOf("https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?w=800"), latitud = -0.2201, longitud = -78.5122))
                repository.insertPunto(PuntoTuristico(recorridoId = rec1Id, orden = 2, nombre = "Punto 2: Basílica del Voto Nacional", descripcion = "Imponente templo neogótico con gárgolas inspiradas en la fauna autóctona.", fotos = listOf("https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=800"), latitud = -0.2147, longitud = -78.5074))
                repository.insertPunto(PuntoTuristico(recorridoId = rec1Id, orden = 3, nombre = "Punto 3: Iglesia de La Compañía de Jesús", descripcion = "Muestra cumbre del barroco con interiores cubiertos en pan de oro.", fotos = listOf("https://images.unsplash.com/photo-1548013146-72479768bada?w=800"), latitud = -0.2215, longitud = -78.5140))
                repository.insertPunto(PuntoTuristico(recorridoId = rec1Id, orden = 4, nombre = "Punto 4: Mirador de El Panecillo", descripcion = "Monumento icónico a la Virgen con vista panorámica de 360 grados.", fotos = listOf("https://images.unsplash.com/photo-1526772662000-3f88f10405ff?w=800"), latitud = -0.2294, longitud = -78.5186))
                repository.insertPunto(PuntoTuristico(recorridoId = rec1Id, orden = 5, nombre = "Punto 5: Barrio Tradicional La Ronda", descripcion = "Callejón bohemio lleno de artesanías, juegos tradicionales y gastronomía.", fotos = listOf("https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=800"), latitud = -0.2248, longitud = -78.5152))

                // Insert 5 Puntos for Recorrido 2
                repository.insertPunto(PuntoTuristico(recorridoId = rec2Id, orden = 1, nombre = "Punto 1: Malecón 2000", descripcion = "Paseo peatonal a orillas del río con monumentos y zonas recreativas.", fotos = listOf("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"), latitud = -2.1950, longitud = -79.8800))
                repository.insertPunto(PuntoTuristico(recorridoId = rec2Id, orden = 2, nombre = "Punto 2: Barrio Las Peñas y Escalinata", descripcion = "Barrio colonial emblemático con 444 escalones y casas coloridas.", fotos = listOf("https://images.unsplash.com/photo-1519046904884-53103b34b206?w=800"), latitud = -2.1811, longitud = -79.8753))
                repository.insertPunto(PuntoTuristico(recorridoId = rec2Id, orden = 3, nombre = "Punto 3: Parque Seminario (Iguanas)", descripcion = "Parque histórico habitado por iguanas verdes en libertad.", fotos = listOf("https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800"), latitud = -2.1953, longitud = -79.8833))
                repository.insertPunto(PuntoTuristico(recorridoId = rec2Id, orden = 4, nombre = "Punto 4: Puerto Santa Ana", descripcion = "Zona moderna gastronómica con faros y paseos turísticos.", fotos = listOf("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800"), latitud = -2.1764, longitud = -79.8739))
                repository.insertPunto(PuntoTuristico(recorridoId = rec2Id, orden = 5, nombre = "Punto 5: Eco-puente Isla Santay", descripcion = "Puente peatonal y reserva natural de flora y fauna tropical.", fotos = listOf("https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800"), latitud = -2.2167, longitud = -79.8667))
            }
        }
    }
}
