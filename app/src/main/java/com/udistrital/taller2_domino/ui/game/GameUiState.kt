package com.udistrital.taller2_domino.ui.game

import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.Tile

data class GameUiState(
    val chain: Chain = Chain(),
    val hand: List<Tile> = emptyList(),
    val playableTiles: Set<Tile> = emptySet(),
    val selectedTile: Tile? = null,
    val enabledSides: Set<Side> = emptySet(),
    val message: String? = null
)