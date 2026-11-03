package com.udistrital.taller2_domino.domain.model

data class Chain(val tiles: List<PlacedTile> = emptyList()) {

    val leftEnd: Int? get() = tiles.firstOrNull()?.left
    val rightEnd: Int? get() = tiles.lastOrNull()?.right
    val isEmpty: Boolean get() = tiles.isEmpty()

    fun endAt(side: Side): Int? = if (side == Side.Left) leftEnd else rightEnd

    fun canPlace(tile: Tile, side: Side): Boolean {
        val end = endAt(side) ?: return true   // mesa vacía: cualquier ficha
        return tile.has(end)
    }

    fun place(tile: Tile, side: Side): Chain {
        require(canPlace(tile, side)) { "La ficha $tile no encaja en el lado $side" }

        if (isEmpty) return Chain(listOf(PlacedTile(tile, tile.low, tile.high)))

        return when (side) {
            Side.Left -> {
                val end = leftEnd!!
                Chain(listOf(PlacedTile(tile, tile.other(end), end)) + tiles)
            }
            Side.Right -> {
                val end = rightEnd!!
                Chain(tiles + PlacedTile(tile, end, tile.other(end)))
            }
        }
    }
}