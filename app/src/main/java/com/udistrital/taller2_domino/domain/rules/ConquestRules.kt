package com.udistrital.taller2_domino.domain.rules

import com.udistrital.taller2_domino.domain.model.PlacedTile
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.Tile

object ConquestRules {
    fun processConquest(
        board: TerritoryBoard,
        row: Int,
        col: Int,
        player: Player,
        tile: Tile? = null
    ): Pair<TerritoryBoard, Int> {
        val square = board.squareAt(row, col) ?: return Pair(board, 0)
        var pointsGained = 0

        val placedTile = if (tile != null) {
            val (dLeft, dRight, isVert) = board.calculateOrientation(tile, row, col)
            PlacedTile(
                tile = tile,
                row = row,
                col = col,
                ownerId = player.id,
                displayLeft = dLeft,
                displayRight = dRight,
                isVertical = isVert
            )
        } else {
            square.placedTile
        }

        val updateSquare = when (square.type) {
            SquareType.Treasure -> {
                pointsGained = 5
                square.copy(ownerId = player.id, placedTile = placedTile)
            }
            SquareType.Bonus -> {
                pointsGained = 3
                square.copy(ownerId = player.id, placedTile = placedTile)
            }
            SquareType.Trap -> {
                pointsGained = -2
                square.copy(ownerId = player.id, placedTile = placedTile)
            }
            SquareType.Fortress -> {
                val newHits = square.fortressHits + 1
                if (newHits >= 2) {
                    pointsGained = 2
                    square.copy(ownerId = player.id, fortressHits = newHits, placedTile = placedTile)
                } else {
                    pointsGained = 1
                    square.copy(fortressHits = newHits, placedTile = placedTile)
                }
            }
            SquareType.Normal, SquareType.Portal -> {
                pointsGained = 1
                square.copy(ownerId = player.id, placedTile = placedTile)
            }
        }
        val newGrid = board.grid.toMutableMap()
        newGrid[Pair(row, col)] = updateSquare
        return Pair(board.copy(grid = newGrid), pointsGained)
    }

    fun playerWinner(players: List<Player>, board: TerritoryBoard): Player? {
        if (players.isEmpty()) return null
        return players.maxWithOrNull(
            compareBy<Player> { it.score }.thenBy { p -> board.grid.values.count { it.ownerId == p.id } }
        )
    }
}