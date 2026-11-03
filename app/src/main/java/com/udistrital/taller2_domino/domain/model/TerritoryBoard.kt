package com.udistrital.taller2_domino.domain.model

data class TerritoryBoard(
    val rows: Int = 6,
    val cols: Int = 6,
    val grid: Map<Pair<Int, Int>, TerritorySquare> = emptyMap()
) {
    fun squareAt(row: Int, col: Int): TerritorySquare? = grid[Pair(row, col)]

    companion object {
        // Genera un tablero inicial distribuyendo casillas especiales aleatoriamente
        fun createRandom(rows: Int = 6, cols: Int = 6): TerritoryBoard {
            val gridMap = mutableMapOf<Pair<Int, Int>, TerritorySquare>()
            val totalSquares = rows * cols

            // Asignamos algunos efectos especiales aleatorios
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val randomValue = (0..100).random()
                    val type = when {
                        randomValue < 8 -> SquareType.Treasure  // 8% de probabilidad de Tesoro
                        randomValue < 15 -> SquareType.Trap     // 7% de probabilidad de Trampa
                        randomValue < 22 -> SquareType.Bonus    // 7% de probabilidad de Bonus
                        randomValue < 28 -> SquareType.Fortress // 6% de probabilidad de Fortaleza
                        randomValue < 33 -> SquareType.Portal   // 5% de probabilidad de Portal
                        else -> SquareType.Normal
                    }
                    gridMap[Pair(r, c)] = TerritorySquare(row = r, col = c, type = type)
                }
            }
            return TerritoryBoard(rows, cols, gridMap)
        }
    }
}