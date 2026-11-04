package com.udistrital.taller2_domino.ui.game

import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.Tile

data class GameUiState(
    val chain: Chain = Chain(),
    val territoryBoard: TerritoryBoard= TerritoryBoard.createRandom(6,6),
    val hand: List<Tile> = emptyList(),
    val players: List<Player> = emptyList(),
    val currentTurnIndex: Int=0,
    val boneyard: List<Tile> = emptyList(), // <-- Fichas que quedan en el pozo
    val selectedTile: Tile? = null,
    val enabledSides: Set<Side> = emptySet(),
    val message: String? = null,
    val isGameOver: Boolean=false,
    val winner: Player?=null
) {
    //Se puede comer si el pozo no está vacío Y el jugador no tiene fichas jugables en la mano
    val canDraw: Boolean get() = boneyard.isNotEmpty() && playableTiles.isEmpty()

    val currentPlayer: Player? get()=players.getOrNull(currentTurnIndex)

    val playableTiles: Set<Tile> get(){
        val hand =currentPlayer?.hand?: return emptySet()
        return hand.filter {chain.canPlace(it, Side.Left)  || chain.canPlace(it, Side.Right)}.toSet()
    }

}