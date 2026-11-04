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

class GameViewModel(
    private val initialHand: List<Tile>? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(newGame())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onTileClicked(tile: Tile) {
        _uiState.update { current ->
            if (current.isGameOver) return@update current

            // 1. Si el tablero está vacío, la primera ficha se coloca automáticamente en una casilla aleatoria
            if (current.territoryBoard.isBoardEmpty) {
                val randomRow = (0 until current.territoryBoard.rows).random()
                val randomCol = (0 until current.territoryBoard.cols).random()
                return@update executeTilePlacement(current, tile, randomRow, randomCol)
            }

            if (current.selectedTile == tile) {
                current.copy(
                    selectedTile = null,
                    validPlacementSquares = emptySet(),
                    enabledSides = emptySet(),
                    message = null
                )
            } else {
                val validSquares = current.territoryBoard.validSquaresFor(tile)
                if (validSquares.isEmpty()) {
                    current.copy(
                        selectedTile = null,
                        validPlacementSquares = emptySet(),
                        enabledSides = emptySet(),
                        message = "Esa ficha no se puede colocar en ninguna casilla adyacente válida"
                    )
                } else {
                    current.copy(
                        selectedTile = tile,
                        validPlacementSquares = validSquares,
                        enabledSides = Side.entries.toSet(),
                        message = "Selecciona una casilla resaltada en verde en el tablero para colocar la ficha"
                    )
                }
            }
        }
    }

    fun onSquareClicked(row: Int, col: Int) {
        _uiState.update { current ->
            val tile = current.selectedTile ?: return@update current
            if (Pair(row, col) !in current.validPlacementSquares || current.isGameOver) return@update current

            executeTilePlacement(current, tile, row, col)
        }
    }

    private fun executeTilePlacement(current: GameUiState, tile: Tile, row: Int, col: Int): GameUiState {
        val currentPlayer = current.currentPlayer ?: return current

        val (newBoard, points) = ConquestRules.processConquest(
            board = current.territoryBoard,
            row = row,
            col = col,
            player = currentPlayer,
            tile = tile
        )

        val updatedHand = currentPlayer.hand - tile
        val updatedPlayer = currentPlayer.copy(
            hand = updatedHand,
            score = currentPlayer.score + points
        )

        val updatedPlayers = current.players.toMutableList()
        updatedPlayers[current.currentTurnIndex] = updatedPlayer

        val isGameOver = updatedHand.isEmpty()
        val winner = if (isGameOver) ConquestRules.playerWinner(updatedPlayers, newBoard) else null
        val message = if (isGameOver) "¡Partida finalizada! Ganador: ${winner?.name}" else null

        val nextTurnIndex = if (isGameOver) current.currentTurnIndex else (current.currentTurnIndex + 1) % current.players.size

        return current.copy(
            territoryBoard = newBoard,
            players = updatedPlayers,
            currentTurnIndex = nextTurnIndex,
            selectedTile = null,
            validPlacementSquares = emptySet(),
            enabledSides = emptySet(),
            message = message,
            isGameOver = isGameOver,
            winner = winner
        )
    }

    fun onSideChosen(side: Side, selectedRow: Int? = null, selectedCol: Int? = null) {
        val validPair = uiState.value.validPlacementSquares.firstOrNull()
        val targetRow = selectedRow ?: validPair?.first ?: 0
        val targetCol = selectedCol ?: validPair?.second ?: 0
        onSquareClicked(targetRow, targetCol)
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

    private fun newGame(): GameUiState {
        val fullSet = DominoSet.full().shuffled()

        val p1Hand = initialHand ?: fullSet.take(7)
        val p2Hand = fullSet.drop(7).take(7)
        val boneyard = fullSet.drop(14)

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