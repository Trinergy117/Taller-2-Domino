package com.udistrital.taller2_domino.ui.game

import androidx.lifecycle.ViewModel
import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.DominoSet
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.Tile
import com.udistrital.taller2_domino.domain.rules.ConquestRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(newGame())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onTileClicked(tile: Tile) {
        _uiState.update { current ->
            if (current.isGameOver) return@update current

            when {
                tile !in current.playableTiles ->
                    current.copy(
                        selectedTile = null,
                        enabledSides = emptySet(),
                        message = "Esa ficha no encaja en la mesa"
                    )

                current.selectedTile == tile ->
                    current.copy(selectedTile = null, enabledSides = emptySet(), message = null)

                else ->
                    current.copy(
                        selectedTile = tile,
                        enabledSides = Side.entries
                            .filter { current.chain.canPlace(tile, it) }
                            .toSet(),
                        message = null
                    )
            }
        }
    }

    fun onSideChosen(side: Side) {
        _uiState.update { current ->
            val tile = current.selectedTile ?: return@update current
            val currentPlayer = current.currentPlayer ?: return@update current
            if (side !in current.enabledSides || current.isGameOver) return@update current

            // 1. Colocamos la ficha en la mesa de dominó
            val newChain = current.chain.place(tile, side)

            // 2. Conquistamos una casilla en el tablero de territorios (fila y col según la cantidad de fichas jugadas)
            val moveCount = newChain.tiles.size
            val targetRow = (moveCount - 1) / 6 % 6
            val targetCol = (moveCount - 1) % 6
            val (newBoard, points) = ConquestRules.processConquest(
                board = current.territoryBoard,
                row = targetRow,
                col = targetCol,
                player = currentPlayer
            )

            // 3. Actualizamos la mano y puntaje del jugador actual
            val updatedHand = currentPlayer.hand - tile
            val updatedPlayer = currentPlayer.copy(
                hand = updatedHand,
                score = currentPlayer.score + points
            )

            val updatedPlayers = current.players.toMutableList()
            updatedPlayers[current.currentTurnIndex] = updatedPlayer

            // 4. Verificamos si la partida terminó (si el jugador se quedó sin fichas)
            val isGameOver = updatedHand.isEmpty()
            val winner = if (isGameOver) ConquestRules.playerWinner(updatedPlayers, newBoard) else null
            val message = if (isGameOver) "¡Partida finalizada! Ganador: ${winner?.name}" else null

            // 5. Alternamos el turno al siguiente jugador
            val nextTurnIndex = if (isGameOver) current.currentTurnIndex else (current.currentTurnIndex + 1) % current.players.size

            current.copy(
                chain = newChain,
                territoryBoard = newBoard,
                players = updatedPlayers,
                currentTurnIndex = nextTurnIndex,
                selectedTile = null,
                enabledSides = emptySet(),
                message = message,
                isGameOver = isGameOver,
                winner = winner
            )
        }
    }

    fun onDrawTile() {
        _uiState.update { current ->
            val currentPlayer = current.currentPlayer ?: return@update current
            if (current.boneyard.isEmpty() || current.isGameOver) return@update current

            val drawnTile = current.boneyard.first()
            val newBoneyard = current.boneyard.drop(1)
            val updatedHand = currentPlayer.hand + drawnTile

            val updatedPlayer = currentPlayer.copy(hand = updatedHand)
            val updatedPlayers = current.players.toMutableList()
            updatedPlayers[current.currentTurnIndex] = updatedPlayer

            current.copy(
                boneyard = newBoneyard,
                players = updatedPlayers
            )
        }
    }

    fun onNewGame() {
        _uiState.value = newGame()
    }

    // ---------- Helpers privados ----------

    private fun newGame(): GameUiState {
        val fullSet = DominoSet.full().shuffled()

        // Repartimos 7 fichas para Jugador 1 y 7 para Jugador 2
        val p1Hand = fullSet.take(7)
        val p2Hand = fullSet.drop(7).take(7)
        val boneyard = fullSet.drop(14) // Las 14 restantes al pozo

        val p1 = Player(id = "p1", name = "Jugador 1", colorHex = 0xFF2196F3, hand = p1Hand)
        val p2 = Player(id = "p2", name = "Jugador 2", colorHex = 0xFFE91E63, hand = p2Hand)

        return GameUiState(
            chain = Chain(),
            territoryBoard = TerritoryBoard.createRandom(6, 6),
            boneyard = boneyard,
            players = listOf(p1, p2),
            currentTurnIndex = 0
        )
    }
}