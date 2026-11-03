package com.udistrital.taller2_domino.ui.game

import androidx.lifecycle.ViewModel
import com.udistrital.taller2_domino.domain.model.Chain
import com.udistrital.taller2_domino.domain.model.DominoSet
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel(
    private val initialHand: List<Tile>? = null   // permite fijar la mano en las pruebas
) : ViewModel() {

    private val _uiState = MutableStateFlow(newGame())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onTileClicked(tile: Tile) {
        _uiState.update { current ->
            when {
                tile !in current.playableTiles ->
                    current.copy(
                        selectedTile = null,
                        enabledSides = emptySet(),
                        message = "Esa ficha no encaja en la mesa"
                    )

                current.selectedTile == tile ->
                    current.copy(selectedTile = null, enabledSides = emptySet(), message = null)

                else ->
                    current.copy(
                        selectedTile = tile,
                        enabledSides = Side.entries
                            .filter { current.chain.canPlace(tile, it) }
                            .toSet(),
                        message = null
                    )
            }
        }
    }

    fun onSideChosen(side: Side) {
        _uiState.update { current ->
            val tile = current.selectedTile ?: return@update current
            if (side !in current.enabledSides) return@update current

            build(
                chain = current.chain.place(tile, side),
                hand = current.hand - tile
            )
        }
    }

    fun onNewGame() {
        _uiState.value = newGame()
    }

    // ---------- Helpers privados ----------

    private fun newGame(): GameUiState =
        build(Chain(), initialHand ?: DominoSet.full().shuffled().take(7))

    private fun build(chain: Chain, hand: List<Tile>): GameUiState {
        val playable = hand
            .filter { chain.canPlace(it, Side.Left) || chain.canPlace(it, Side.Right) }
            .toSet()
        return GameUiState(
            chain = chain,
            hand = hand,
            playableTiles = playable,
            message = if (hand.isEmpty()) "¡Te quedaste sin fichas!" else null
        )
    }
}