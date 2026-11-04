package com.udistrital.taller2_domino.ui.auth

import com.google.firebase.auth.FirebaseUser

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: FirebaseUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}