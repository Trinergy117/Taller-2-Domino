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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
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
    val buttonShape = RoundedCornerShape(14.dp)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌐 Multijugador", fontSize = 36.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Crea o únete a una partida online",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState is LobbyUiState.WaitingForGuest) {
            Text("🎉 ¡Sala Creada!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Comparte este código de 6 dígitos con tu rival:", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(
                    text = uiState.roomCode,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Esperando que se una el segundo jugador...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onResetState) {
                Text("Cancelar Sala", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onCreateRoom,
                enabled = uiState !is LobbyUiState.CreatingRoom && uiState !is LobbyUiState.JoiningRoom,
                shape = buttonShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, buttonShape)
            ) {
                Text(
                    text = if (uiState is LobbyUiState.CreatingRoom) "Creando sala..." else "✨ Crear Nueva Sala",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = " O UNIRSE A UNA SALA ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
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
                shape = buttonShape,
                colors = textFieldColors,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = { onJoinRoom(inputCode) },
                enabled = inputCode.length == 6 && uiState !is LobbyUiState.JoiningRoom,
                shape = buttonShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (uiState is LobbyUiState.JoiningRoom) "Uniéndose..." else "🚀 Unirse a Sala",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState is LobbyUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onBackToMenu) {
                Text("Volver al Menú Principal", fontWeight = FontWeight.SemiBold)
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
