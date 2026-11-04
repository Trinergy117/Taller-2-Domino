package com.udistrital.taller2_domino.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Base de datos local de usuarios registrados (inicia con el usuario de prueba)
    private val registeredUsers = mutableMapOf(
        "david080221@gmail.com" to "1234cincoseis"
    )

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email.trim(), errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password.trim(), errorMessage = null) }
    }

    fun onLoginClicked() {
        val current = _uiState.value
        if (current.email.isEmpty() || current.password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingrese correo y contraseña") }
            return
        }

        val expectedPassword = registeredUsers[current.email]
        if (expectedPassword == null) {
            _uiState.update { it.copy(errorMessage = "El usuario no existe. Regístrese primero.") }
        } else if (expectedPassword != current.password) {
            _uiState.update { it.copy(errorMessage = "Contraseña incorrecta") }
        } else {
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    userEmail = current.email,
                    errorMessage = null
                )
            }
        }
    }

    fun onRegisterClicked() {
        val current = _uiState.value
        if (current.email.isEmpty() || current.password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Ingrese correo y contraseña para registrarse") }
            return
        }

        if (registeredUsers.containsKey(current.email)) {
            _uiState.update { it.copy(errorMessage = "El usuario ya está registrado. Inicie sesión.") }
        } else {
            registeredUsers[current.email] = current.password
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    userEmail = current.email,
                    errorMessage = "¡Registro exitoso! Sesión iniciada."
                )
            }
        }
    }
}