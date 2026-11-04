package com.udistrital.taller2_domino.domain.model

data class TerritorySquare(
    val row: Int,
    val col: Int,
    val type: SquareType = SquareType.Normal,
    val ownerId: String? = null,
    val fortressHits: Int = 0,
    val placedTile: PlacedTile? = null
) {
    val isControlled: Boolean get() = if (type == SquareType.Fortress) fortressHits >= 2 else ownerId != null
    val isEmpty: Boolean get() = placedTile == null
}