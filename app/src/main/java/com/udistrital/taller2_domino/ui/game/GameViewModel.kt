package com.udistrital.taller2_domino.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.taller2_domino.data.model.GameRoomDto
import com.udistrital.taller2_domino.data.model.toDomain
import com.udistrital.taller2_domino.data.model.toDto
import com.udistrital.taller2_domino.data.model.toSquareDtoList
import com.udistrital.taller2_domino.data.repository.FirebaseMultiplayerRepository
import com.udistrital.taller2_domino.data.repository.MultiplayerRepository
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
import kotlinx.coroutines.launch

class GameViewModel(
    val roomCode: String? = null,
    val localUserId: String? = null,
    private val multiplayerRepository: MultiplayerRepository? = if (!roomCode.isNullOrBlank()) FirebaseMultiplayerRepository() else null,
    private val initialHand: List<Tile>? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(newGame())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        if (!roomCode.isNullOrBlank()) {
            _uiState.update { it.copy(roomCode = roomCode, localUserId = localUserId) }
            listenToOnlineRoom(roomCode)
        }
    }

    private fun listenToOnlineRoom(code: String) {
        viewModelScope.launch {
            multiplayerRepository?.listenToRoom(code)?.collect { roomDto ->
                if (roomDto != null) {
                    _uiState.update { current ->
                        val board = roomDto.toTerritoryBoard()
                        val players = roomDto.players.map { it.toDomain() }
                        val boneyard = roomDto.boneyard.map { it.toDomain() }
                        val isGameOver = roomDto.status == "FINISHED"
                        val winner = if (isGameOver) players.find { it.id == roomDto.winnerId } else null

                        current.copy(
                            territoryBoard = board,
                            players = players,
                            currentTurnIndex = roomDto.currentTurnIndex,
                            boneyard = boneyard,
                            isGameOver = isGameOver,
                            winner = winner
                        )
                    }
                }
            }
        }
    }

    fun onTileClicked(tile: Tile) {
        val current = _uiState.value
        if (current.isGameOver) return
        if (!current.isMyTurn) {
            _uiState.update { it.copy(message = "No es tu turno. Espera a tu rival.") }
            return
        }

        _uiState.update { state ->
            if (state.territoryBoard.isBoardEmpty) {
                val randomRow = (0 until state.territoryBoard.rows).random()
                val randomCol = (0 until state.territoryBoard.cols).random()
                return@update executeTilePlacement(state, tile, randomRow, randomCol)
            }

            if (state.selectedTile == tile) {
                state.copy(
                    selectedTile = null,
                    validPlacementSquares = emptySet(),
                    enabledSides = emptySet(),
                    message = null
                )
            } else {
                val validSquares = state.territoryBoard.validSquaresFor(tile)
                if (validSquares.isEmpty()) {
                    state.copy(
                        selectedTile = null,
                        validPlacementSquares = emptySet(),
                        enabledSides = emptySet(),
                        message = "Esa ficha no se puede colocar en ninguna casilla adyacente válida"
                    )
                } else {
                    state.copy(
                        selectedTile = tile,
                        validPlacementSquares = validSquares,
                        enabledSides = Side.entries.toSet(),
                        message = "Selecciona una casilla resaltada en verde para colocar la ficha"
                    )
                }
            }
        }
    }

    fun onSquareClicked(row: Int, col: Int) {
        val current = _uiState.value
        if (!current.isMyTurn || current.isGameOver) {
            _uiState.update { it.copy(message = "No es tu turno. Espera a tu rival.") }
            return
        }
        val tile = current.selectedTile ?: return
        if (Pair(row, col) !in current.validPlacementSquares) return

        val newState = executeTilePlacement(current, tile, row, col)
        _uiState.value = newState

        if (newState.isOnlineGame && roomCode != null) {
            syncStateToFirestore(roomCode, newState)
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
        val current = _uiState.value
        if (!current.isMyTurn || current.boneyard.isEmpty() || current.isGameOver) return

        val drawnTile = current.boneyard.first()
        val newBoneyard = current.boneyard.drop(1)
        val updatedHand = current.localHand + drawnTile

        val updatedPlayer = (current.localPlayer ?: return).copy(hand = updatedHand)
        val updatedPlayers = current.players.toMutableList()
        val playerIndex = updatedPlayers.indexOfFirst { it.id == updatedPlayer.id }
        if (playerIndex != -1) {
            updatedPlayers[playerIndex] = updatedPlayer
        }

        val newState = current.copy(
            boneyard = newBoneyard,
            players = updatedPlayers
        )
        _uiState.value = newState

        if (newState.isOnlineGame && roomCode != null) {
            syncStateToFirestore(roomCode, newState)
        }
    }

    private fun syncStateToFirestore(code: String, state: GameUiState) {
        viewModelScope.launch {
            val roomDto = GameRoomDto(
                code = code,
                hostId = state.players.getOrNull(0)?.id ?: "",
                hostName = state.players.getOrNull(0)?.name ?: "",
                guestId = state.players.getOrNull(1)?.id ?: "",
                guestName = state.players.getOrNull(1)?.name ?: "",
                status = if (state.isGameOver) "FINISHED" else "PLAYING",
                currentTurnIndex = state.currentTurnIndex,
                boneyard = state.boneyard.map { it.toDto() },
                territoryBoardRows = state.territoryBoard.rows,
                territoryBoardCols = state.territoryBoard.cols,
                territorySquares = state.territoryBoard.toSquareDtoList(),
                players = state.players.map { it.toDto() },
                winnerId = state.winner?.id
            )
            multiplayerRepository?.updateGameState(code, roomDto)
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
            roomCode = roomCode,
            localUserId = localUserId,
            chain = Chain(),
            territoryBoard = TerritoryBoard.createRandom(6, 6),
            boneyard = boneyard,
            players = listOf(p1, p2),
            currentTurnIndex = 0
        )
    }
}
