package com.cormo.neulbeot.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)
    private val _state = MutableStateFlow(AuthState.unauth())
    val state: StateFlow<AuthState> = _state

    init {
        viewModelScope.launch {
            _state.value = repo.loadSaved()
        }
    }

    fun login(username: String, password: String, onError: (Throwable) -> Unit = {}) {
        viewModelScope.launch {
            runCatching { repo.login(username, password) }
                .onSuccess { _state.value = it }
                .onFailure(onError)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _state.value = AuthState.unauth()
        }
    }
}
