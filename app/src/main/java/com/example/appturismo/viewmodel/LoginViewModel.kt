package com.example.appturismo.viewmodel

import androidx.lifecycle.*
import com.example.appturismo.data.model.User
import com.example.appturismo.data.repository.TurismoRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: TurismoRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            _loginResult.postValue(user)
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(username)
            if (existing == null) {
                repository.register(User(username = username, password = password, role = "USER"))
                _registerResult.postValue(true)
            } else {
                _registerResult.postValue(false)
            }
        }
    }
}

class LoginViewModelFactory(private val repository: TurismoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
