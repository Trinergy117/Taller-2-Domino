package com.udistrital.taller2_domino.ui.game

import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.Tile

data class GameUiState(
    val chain: Chain = Chain(),
    val hand: List<Tile> = emptyList(),
    val boneyard: List<Tile> = emptyList(), // <-- Fichas restantes en el pozo
    val playableTiles: Set<Tile> = emptySet(),
    val selectedTile: Tile? = null,
    val enabledSides: Set<Side> = emptySet(),
    val message: String? = null
) {
    // Propiedad calculada: Se puede comer si el pozo no está vacío Y el jugador no tiene fichas jugables en la mano
    val canDraw: Boolean get() = boneyard.isNotEmpty() && playableTiles.isEmpty()
}