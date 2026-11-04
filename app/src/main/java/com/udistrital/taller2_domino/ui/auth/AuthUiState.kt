package com.udistrital.taller2_domino.ui.auth

data class AuthUiState(
    val email: String = "david080221@gmail.com",
    val password: String = "1234cincoseis",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val userEmail: String? = null
)