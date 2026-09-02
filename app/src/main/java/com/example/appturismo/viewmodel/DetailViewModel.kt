package com.example.appturismo.viewmodel

import androidx.lifecycle.*
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: TurismoRepository) : ViewModel() {

    private val _punto = MutableLiveData<PuntoTuristico?>()
    val punto: LiveData<PuntoTuristico?> = _punto

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun loadPunto(id: Int, userId: Int) {
        viewModelScope.launch {
            val p = repository.getPuntoById(id)
            _punto.postValue(p)
            _isFavorite.postValue(repository.isFavorite(userId, id))
        }
    }

    fun toggleFavorite(userId: Int, puntoId: Int) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(userId, puntoId)
            val currentPunto = _punto.value
            if (isFav) {
                repository.removeFavorite(userId, puntoId)
                currentPunto?.let { repository.updatePunto(it.copy(esFavorito = false)) }
                _isFavorite.postValue(false)
            } else {
                repository.addFavorite(userId, puntoId)
                currentPunto?.let { repository.updatePunto(it.copy(esFavorito = true)) }
                _isFavorite.postValue(true)
            }
        }
    }
}

class DetailViewModelFactory(private val repository: TurismoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
