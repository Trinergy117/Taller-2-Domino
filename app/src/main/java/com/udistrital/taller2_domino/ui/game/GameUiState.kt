package com.udistrital.taller2_domino.ui.game

import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.Tile

data class GameUiState(
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
    val winner: Player? = null
) {
    val canDraw: Boolean get() = boneyard.isNotEmpty() && playableTiles.isEmpty()
    val currentPlayer: Player? get() = players.getOrNull(currentTurnIndex)
    val hand: List<Tile> get() = currentPlayer?.hand ?: emptyList()

    val playableTiles: Set<Tile> get() {
        val currentHand = currentPlayer?.hand ?: return emptySet()
        if (territoryBoard.isBoardEmpty) return currentHand.toSet()
        return currentHand.filter { territoryBoard.validSquaresFor(it).isNotEmpty() }.toSet()
    }
}