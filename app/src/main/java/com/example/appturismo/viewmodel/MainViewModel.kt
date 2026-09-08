package com.example.appturismo.viewmodel

import androidx.lifecycle.*
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class MainViewModel(private val repository: TurismoRepository) : ViewModel() {

    val allRecorridos: LiveData<List<com.example.appturismo.data.model.Recorrido>> = repository.allRecorridos.asLiveData()
    val allPuntos: LiveData<List<PuntoTuristico>> = repository.allPuntos.asLiveData()

    fun getPuntosByRecorrido(recorridoId: Int): LiveData<List<PuntoTuristico>> {
        return repository.getPuntosByRecorrido(recorridoId).asLiveData()
    }

    private val _syncing = MutableLiveData<Boolean>()
    val syncing: LiveData<Boolean> = _syncing

    fun sync() {
        viewModelScope.launch {
            _syncing.value = true
            repository.syncData()
            _syncing.value = false
        }
    }

    fun toggleFavorite(userId: Int, punto: PuntoTuristico) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(userId, punto.id)
            if (isFav) {
                repository.removeFavorite(userId, punto.id)
                repository.updatePunto(punto.copy(esFavorito = false))
            } else {
                repository.addFavorite(userId, punto.id)
                repository.updatePunto(punto.copy(esFavorito = true))
            }
        }
    }

    fun getFavorites(userId: Int): LiveData<List<PuntoTuristico>> {
        return repository.getFavorites(userId).asLiveData()
    }
}

class MainViewModelFactory(private val repository: TurismoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
