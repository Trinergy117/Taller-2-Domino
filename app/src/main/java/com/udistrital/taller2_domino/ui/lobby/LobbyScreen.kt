package com.udistrital.taller2_domino.ui.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser

@Composable
fun LobbyScreen(
    currentUser: FirebaseUser?,
    onRoomJoined: (roomCode: String) -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    val uiState by lobbyViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is LobbyUiState.RoomReady) {
            val code = (uiState as LobbyUiState.RoomReady).roomCode
            onRoomJoined(code)
        }
    }

    LobbyScreenContent(
        uiState = uiState,
        onCreateRoom = { lobbyViewModel.createRoom(currentUser) },
        onJoinRoom = { code -> lobbyViewModel.joinRoom(code, currentUser) },
        onResetState = lobbyViewModel::resetState,
        onBackToMenu = onBackToMenu,
        modifier = modifier
    )
}

@Composable
fun LobbyScreenContent(
    uiState: LobbyUiState,
    onCreateRoom: () -> Unit,
    onJoinRoom: (code: String) -> Unit,
    onResetState: () -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Multijugador", style = MaterialTheme.typography.displayMedium)
        Text(
            text = "Crea o únete a una partida online",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState is LobbyUiState.WaitingForGuest) {
            Text("¡Sala Creada!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Comparte este código con tu rival:", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = uiState.roomCode,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Esperando que se una el segundo jugador...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onResetState) {
                Text("Cancelar Sala")
            }
        } else {
            Button(
                onClick = onCreateRoom,
                enabled = uiState !is LobbyUiState.CreatingRoom && uiState !is LobbyUiState.JoiningRoom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState is LobbyUiState.CreatingRoom) "Creando sala..." else "Crear Nueva Sala")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = " O UNIRSE ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            OutlinedTextField(
                value = inputCode,
                onValueChange = { if (it.length <= 6) inputCode = it },
                label = { Text("Código de Sala (6 dígitos)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onJoinRoom(inputCode) },
                enabled = inputCode.length == 6 && uiState !is LobbyUiState.JoiningRoom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState is LobbyUiState.JoiningRoom) "Uniéndose..." else "Unirse a Sala")
            }

            if (uiState is LobbyUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onBackToMenu) {
                Text("Volver al Menú Principal")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LobbyScreenPreview() {
    LobbyScreenContent(
        uiState = LobbyUiState.Idle,
        onCreateRoom = {},
        onJoinRoom = {},
        onResetState = {},
        onBackToMenu = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LobbyScreenWaitingPreview() {
    LobbyScreenContent(
        uiState = LobbyUiState.WaitingForGuest("583920"),
        onCreateRoom = {},
        onJoinRoom = {},
        onResetState = {},
        onBackToMenu = {}
    )
}
