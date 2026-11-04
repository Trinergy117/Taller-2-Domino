package com.udistrital.taller2_domino.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.udistrital.taller2_domino.data.repository.FirebaseMultiplayerRepository
import com.udistrital.taller2_domino.data.repository.MultiplayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LobbyUiState {
    data object Idle : LobbyUiState
    data object CreatingRoom : LobbyUiState
    data class WaitingForGuest(val roomCode: String) : LobbyUiState
    data object JoiningRoom : LobbyUiState
    data class RoomReady(val roomCode: String) : LobbyUiState
    data class Error(val message: String) : LobbyUiState
}

class LobbyViewModel(
    private val multiplayerRepository: MultiplayerRepository = FirebaseMultiplayerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LobbyUiState>(LobbyUiState.Idle)
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    private var listenJob: Job? = null

    fun createRoom(user: FirebaseUser?) {
        val hostId = user?.uid ?: "anon_${System.currentTimeMillis()}"
        val hostName = user?.email?.substringBefore("@")
            ?: if (user?.isAnonymous == true) "Invitado" else "Jugador 1"

        viewModelScope.launch {
            _uiState.value = LobbyUiState.CreatingRoom
            multiplayerRepository.createRoom(hostId, hostName)
                .onSuccess { roomCode ->
                    _uiState.value = LobbyUiState.WaitingForGuest(roomCode)
                    startListeningForGuest(roomCode)
                }
                .onFailure { exception ->
                    _uiState.value = LobbyUiState.Error(
                        exception.localizedMessage ?: "Error al crear la sala"
                    )
                }
        }
    }

    private fun startListeningForGuest(roomCode: String) {
        listenJob?.cancel()
        listenJob = viewModelScope.launch {
            multiplayerRepository.listenToRoom(roomCode).collect { roomDto ->
                if (roomDto != null && roomDto.status == "PLAYING") {
                    _uiState.value = LobbyUiState.RoomReady(roomCode)
                }
            }
        }
    }

    fun joinRoom(code: String, user: FirebaseUser?) {
        val trimmedCode = code.trim()
        if (trimmedCode.length != 6) {
            _uiState.value = LobbyUiState.Error("El código de sala debe tener 6 dígitos")
            return
        }

        val guestId = user?.uid ?: "anon_${System.currentTimeMillis()}"
        val guestName = user?.email?.substringBefore("@")
            ?: if (user?.isAnonymous == true) "Invitado" else "Jugador 2"

        viewModelScope.launch {
            _uiState.value = LobbyUiState.JoiningRoom
            multiplayerRepository.joinRoom(trimmedCode, guestId, guestName)
                .onSuccess {
                    _uiState.value = LobbyUiState.RoomReady(trimmedCode)
                }
                .onFailure { exception ->
                    _uiState.value = LobbyUiState.Error(
                        exception.localizedMessage ?: "Error al unirse a la sala"
                    )
                }
        }
    }

    fun resetState() {
        listenJob?.cancel()
        _uiState.value = LobbyUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        listenJob?.cancel()
    }
}
