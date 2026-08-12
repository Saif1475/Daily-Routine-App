package com.dailyroutine.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyroutine.app.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel @JvmOverloads constructor(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = repository.authState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.currentUser)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.signIn(email, password)
                .onFailure { _error.value = it.message ?: "Sign in failed" }
            _isLoading.value = false
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.signUp(email, password, name)
                .onFailure { _error.value = it.message ?: "Sign up failed" }
            _isLoading.value = false
        }
    }

    fun signOut() = repository.signOut()

    fun clearError() { _error.value = null }
}
