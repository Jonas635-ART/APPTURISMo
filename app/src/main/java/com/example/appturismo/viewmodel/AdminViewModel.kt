package com.example.appturismo.viewmodel

import androidx.lifecycle.*
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.data.model.User
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class AdminViewModel(private val repository: TurismoRepository) : ViewModel() {

    val allPuntos: LiveData<List<PuntoTuristico>> = repository.allPuntos.asLiveData()
    val admins: LiveData<List<User>> = repository.getAdmins().asLiveData()

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
