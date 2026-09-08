package com.example.appturismo.viewmodel

import androidx.lifecycle.*
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.User
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class AdminViewModel(private val repository: TurismoRepository) : ViewModel() {

    val allRecorridos: LiveData<List<com.example.appturismo.data.model.Recorrido>> = repository.allRecorridos.asLiveData()
    val allPuntos: LiveData<List<PuntoTuristico>> = repository.allPuntos.asLiveData()
    val admins: LiveData<List<User>> = repository.getAdmins().asLiveData()

    fun addRecorrido(nombre: String, descripcion: String) {
        viewModelScope.launch {
            repository.insertRecorrido(
                com.example.appturismo.data.model.Recorrido(
                    nombre = nombre,
                    descripcion = descripcion,
                    region = "Nacional",
                    duracionEstimada = "3 Horas",
                    distanciaTotal = "10 km",
                    urlImagen = "https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?w=800"
                )
            )
        }
    }

    fun addAdmin(username: String, password: String) {
        viewModelScope.launch {
            repository.register(User(username = username, password = password, role = "ADMIN"))
        }
    }

    fun deletePunto(punto: PuntoTuristico) {
        viewModelScope.launch {
            repository.deletePunto(punto)
        }
    }

    fun addPunto(punto: PuntoTuristico) {
        viewModelScope.launch {
            repository.insertPunto(punto)
        }
    }

    fun updatePunto(punto: PuntoTuristico) {
        viewModelScope.launch {
            repository.updatePunto(punto)
        }
    }
}

class AdminViewModelFactory(private val repository: TurismoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
