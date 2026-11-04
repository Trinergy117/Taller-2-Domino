package com.udistrital.taller2_domino.domain.model

data class PlacedTile(
    val tile: Tile,
    val row: Int = 0,
    val col: Int = 0,
    val ownerId: String = "",
    val displayLeft: Int = tile.low,
    val displayRight: Int = tile.high,
    val isVertical: Boolean = false,
    val left: Int = displayLeft,
    val right: Int = displayRight
)