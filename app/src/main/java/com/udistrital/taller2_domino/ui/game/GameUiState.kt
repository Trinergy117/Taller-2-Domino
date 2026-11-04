package com.udistrital.taller2_domino.ui.game

import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.Tile

data class GameUiState(
    val roomCode: String? = null,
    val localUserId: String? = null,
    val chain: Chain = Chain(),
    val territoryBoard: TerritoryBoard = TerritoryBoard.createRandom(6, 6),
    val players: List<Player> = emptyList(),
    val currentTurnIndex: Int = 0,
    val boneyard: List<Tile> = emptyList(),
    val selectedTile: Tile? = null,
    val validPlacementSquares: Set<Pair<Int, Int>> = emptySet(),
    val enabledSides: Set<Side> = emptySet(),
    val message: String? = null,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val isAbandoned: Boolean = false,
    val abandonedByName: String? = null,
    val restartRequestedBy: String? = null,
    val restartRequestedByName: String? = null
) {
    val isOnlineGame: Boolean get() = !roomCode.isNullOrBlank()
    val currentPlayer: Player? get() = players.getOrNull(currentTurnIndex)
    val isMyTurn: Boolean get() = !isOnlineGame || (currentPlayer != null && currentPlayer?.id == localUserId)

    val localPlayer: Player? get() = if (isOnlineGame) players.find { it.id == localUserId } else currentPlayer
    val localHand: List<Tile> get() = localPlayer?.hand ?: emptyList()

    val isRestartRequestedByOpponent: Boolean get() = isOnlineGame && restartRequestedBy != null && restartRequestedBy != localUserId
    val isWaitingRestartResponse: Boolean get() = isOnlineGame && restartRequestedBy != null && restartRequestedBy == localUserId

    val canDraw: Boolean get() = boneyard.isNotEmpty() && playableTiles.isEmpty() && isMyTurn

    val playableTiles: Set<Tile> get() {
        val currentHand = localHand
        if (territoryBoard.isBoardEmpty) return currentHand.toSet()
        return currentHand.filter { territoryBoard.validSquaresFor(it).isNotEmpty() }.toSet()
    }
}
