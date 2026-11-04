package com.udistrital.taller2_domino.domain.model

data class TerritoryBoard(
    val rows: Int = 6,
    val cols: Int = 6,
    val grid: Map<Pair<Int, Int>, TerritorySquare> = emptyMap()
) {
    fun squareAt(row: Int, col: Int): TerritorySquare? = grid[Pair(row, col)]

    val isBoardEmpty: Boolean get() = grid.values.none { it.placedTile != null }

    /**
     * Retorna las coordenadas (row, col) de las casillas en el tablero donde es válido colocar la ficha [tile].
     */
    fun validSquaresFor(tile: Tile): Set<Pair<Int, Int>> {
        val valid = mutableSetOf<Pair<Int, Int>>()

        if (isBoardEmpty) {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    valid.add(Pair(r, c))
                }
            }
            return valid
        }

        val directions = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val currentSquare = squareAt(r, c) ?: continue
                if (currentSquare.placedTile != null) continue

                var matchesNeighbor = false
                for ((dr, dc) in directions) {
                    val neighborSquare = squareAt(r + dr, c + dc) ?: continue
                    val neighborPlaced = neighborSquare.placedTile ?: continue
                    val neighborTile = neighborPlaced.tile

                    if (tile.has(neighborTile.low) || tile.has(neighborTile.high)) {
                        matchesNeighbor = true
                        break
                    }
                }

                if (matchesNeighbor) {
                    valid.add(Pair(r, c))
                }
            }
        }

        return valid
    }

    /**
     * Calcula la orientación adecuada (displayLeft, displayRight, isVertical) para que las caras coincidentes queden enfrentadas.
     */
    fun calculateOrientation(tile: Tile, row: Int, col: Int): Triple<Int, Int, Boolean> {
        if (isBoardEmpty) {
            return Triple(tile.low, tile.high, false)
        }

        val directions = listOf(
            Pair(0, -1) to "LEFT",
            Pair(0, 1) to "RIGHT",
            Pair(-1, 0) to "TOP",
            Pair(1, 0) to "BOTTOM"
        )

        for ((dir, posRelativeToNeighbor) in directions) {
            val (dr, dc) = dir
            val neighborSquare = squareAt(row + dr, col + dc) ?: continue
            val neighborPlaced = neighborSquare.placedTile ?: continue
            val neighborTile = neighborPlaced.tile

            val matchingValue = when {
                tile.has(neighborPlaced.displayLeft) -> neighborPlaced.displayLeft
                tile.has(neighborPlaced.displayRight) -> neighborPlaced.displayRight
                tile.has(neighborTile.low) -> neighborTile.low
                tile.has(neighborTile.high) -> neighborTile.high
                else -> continue
            }

            val otherValue = tile.other(matchingValue)

            return when (posRelativeToNeighbor) {
                "RIGHT" -> Triple(otherValue, matchingValue, false) // Ficha a la izquierda del vecino: matchingValue a la derecha
                "LEFT" -> Triple(matchingValue, otherValue, false)  // Ficha a la derecha del vecino: matchingValue a la izquierda
                "BOTTOM" -> Triple(otherValue, matchingValue, true) // Ficha arriba del vecino: matchingValue abajo (vertical)
                "TOP" -> Triple(matchingValue, otherValue, true)    // Ficha abajo del vecino: matchingValue arriba (vertical)
                else -> Triple(tile.low, tile.high, false)
            }
        }

        return Triple(tile.low, tile.high, false)
    }

    companion object {
        fun createRandom(rows: Int = 6, cols: Int = 6): TerritoryBoard {
            val gridMap = mutableMapOf<Pair<Int, Int>, TerritorySquare>()

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val randomValue = (0..100).random()
                    val type = when {
                        randomValue < 8 -> SquareType.Treasure
                        randomValue < 15 -> SquareType.Trap
                        randomValue < 22 -> SquareType.Bonus
                        randomValue < 28 -> SquareType.Fortress
                        randomValue < 33 -> SquareType.Portal
                        else -> SquareType.Normal
                    }
                    gridMap[Pair(r, c)] = TerritorySquare(row = r, col = c, type = type)
                }
            }
            return TerritoryBoard(rows, cols, gridMap)
        }
    }
}