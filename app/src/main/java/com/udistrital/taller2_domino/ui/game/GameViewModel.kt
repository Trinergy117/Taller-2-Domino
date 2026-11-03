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
                hand = current.hand - tile,
                boneyard = current.boneyard
            )
        }
    }

    fun onNewGame() {
        _uiState.value = newGame()
    }

    private fun newGame(): GameUiState {
        val fullSet = (initialHand ?: DominoSet.full()).shuffled()
        val hand = fullSet.take(7)
        val boneyard = fullSet.drop(7) // Las 21 fichas restantes van al pozo
        return build(Chain(), hand, boneyard)
    }
    private fun build(chain: Chain, hand: List<Tile>, boneyard: List<Tile>): GameUiState {
        val playable = hand
            .filter { chain.canPlace(it, Side.Left) || chain.canPlace(it, Side.Right) }
            .toSet()

        val message = when {
            hand.isEmpty() -> "¡Te quedaste sin fichas! ¡Ganaste!"
            playable.isEmpty() && boneyard.isNotEmpty() -> "No tienes fichas jugables. ¡Roba una del pozo!"
            playable.isEmpty() && boneyard.isEmpty() -> "No tienes jugadas ni fichas en el pozo. ¡Pasa turno!"
            else -> null
        }

        return GameUiState(
            chain = chain,
            hand = hand,
            boneyard = boneyard,
            playableTiles = playable,
            message = message
        )
    }

    fun onDrawTile(){
        _uiState.update {current ->
            if (current.boneyard.isEmpty()) return@update current

            val drawTile=current.boneyard.first()
            val newBoneyard= current.boneyard.drop(1)
            val newHand= current.hand + drawTile

            build(current.chain, newHand, newBoneyard)

        }
    }


}