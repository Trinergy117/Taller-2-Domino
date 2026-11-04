package com.udistrital.taller2_domino.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udistrital.taller2_domino.ui.components.HandView
import com.udistrital.taller2_domino.ui.components.ScoreboardView
import com.udistrital.taller2_domino.ui.components.TerritoryBoardView
import com.udistrital.taller2_domino.ui.game.GameViewModel

@Composable
fun GameScreen(
    roomCode: String? = null,
    localUserId: String? = null,
    onBackToMenu: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(
        key = roomCode ?: "local",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(roomCode = roomCode, localUserId = localUserId) as T
            }
        }
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showAbandonDialog by remember { mutableStateOf(false) }

    // DIÁLOGO DE CONFIRMACIÓN PARA ABANDONAR
    if (showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { showAbandonDialog = false },
            title = { Text("Abandonar Sala") },
            text = { Text("¿Estás seguro de que deseas abandonar la partida online?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAbandonDialog = false
                        viewModel.onAbandonGame()
                        onBackToMenu()
                    }
                ) {
                    Text("Sí, Abandonar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIÁLOGO CUANDO EL OPONENTE ABANDONA
    if (state.isOnlineGame && state.isAbandoned && state.localUserId != null && state.abandonedByName != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Partida Finalizada") },
            text = { Text("El oponente ${state.abandonedByName} ha abandonado la sala.") },
            confirmButton = {
                Button(onClick = onBackToMenu) {
                    Text("Volver al Menú Principal")
                }
            }
        )
    }

    // DIÁLOGO CUANDO EL OPONENTE SOLICITA REVANCHA / REINICIO
    if (state.isRestartRequestedByOpponent) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Solicitud de Revancha!") },
            text = { Text("El oponente ${state.restartRequestedByName ?: "rival"} ha solicitado una revancha. ¿Deseas aceptar o salir al menú?") },
            confirmButton = {
                Button(onClick = viewModel::onAcceptRestart) {
                    Text("Aceptar Revancha")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.onAbandonGame()
                        onBackToMenu()
                    }
                ) {
                    Text("Abandonar Sala", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BANNER DE MODO ONLINE
        if (state.isOnlineGame) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when {
                            state.isWaitingRestartResponse -> MaterialTheme.colorScheme.secondaryContainer
                            state.isMyTurn -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        state.isWaitingRestartResponse -> "⌛ Solicitud enviada. Esperando al rival..."
                        state.isGameOver -> "🏁 ¡Partida Finalizada!"
                        state.isMyTurn -> "🟢 ¡Es tu turno!"
                        else -> "⌛ Turno de ${state.currentPlayer?.name ?: "rival"}..."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        state.isWaitingRestartResponse -> MaterialTheme.colorScheme.onSecondaryContainer
                        state.isMyTurn -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // 1. MARCADOR Y TURNO SUPERIOR
        ScoreboardView(
            players = state.players,
            currentTurnIndex = state.currentTurnIndex
        )

        Spacer(Modifier.height(12.dp))

        // 2. TABLERO DE DÓMINO Y CONQUISTAS (6x6 GRANDES EN PANTALLA)
        Text(
            text = "Tablero de Juego y Conquistas (6x6)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TerritoryBoardView(
            board = state.territoryBoard,
            players = state.players,
            validPlacementSquares = state.validPlacementSquares,
            onSquareClick = { r, c ->
                viewModel.onSquareClicked(r, c)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // MENSAJES Y ANUNCIOS
        state.message?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        // 3. POZO Y BOTÓN DE COMER FICHA
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pozo: ${state.boneyard.size} fichas",
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedButton(
                onClick = viewModel::onDrawTile,
                enabled = state.canDraw && !state.isGameOver
            ) {
                Text("Comer ficha")
            }
        }

        Spacer(Modifier.height(12.dp))

        // 4. MANO DEL JUGADOR
        Text(
            text = "Tu Mano (${state.localPlayer?.name ?: ""})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))

        HandView(
            hand = state.localHand,
            playableTiles = state.playableTiles,
            selectedTile = state.selectedTile,
            onTileClick = viewModel::onTileClicked
        )

        Spacer(Modifier.height(16.dp))

        if (!state.isOnlineGame) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(onClick = viewModel::onNewGame) { Text("Nueva partida") }
                TextButton(onClick = onBackToMenu) {
                    Text(
                        text = "Volver al Menú Principal",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = viewModel::onRequestRestart,
                    enabled = !state.isWaitingRestartResponse
                ) {
                    Text(if (state.isGameOver) "Revancha" else "Reiniciar Partida")
                }
                OutlinedButton(
                    onClick = {
                        if (state.isGameOver) {
                            viewModel.onAbandonGame()
                            onBackToMenu()
                        } else {
                            showAbandonDialog = true
                        }
                    }
                ) {
                    Text("Abandonar Sala", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
