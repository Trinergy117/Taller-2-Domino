package com.udistrital.taller2_domino

import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.TerritorySquare
import com.udistrital.taller2_domino.domain.model.Tile
import com.udistrital.taller2_domino.ui.game.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {

    private val hand = listOf(Tile(3, 5), Tile(5, 6), Tile(0, 2))

    @Test
    fun startsWithGivenHandAndEmptyChain() {
        val vm = GameViewModel(initialHand = hand)
        assertEquals(3, vm.uiState.value.players[0].hand.size)
        assertEquals(true, vm.uiState.value.territoryBoard.isBoardEmpty)
    }

    @Test
    fun playingFirstTilePlacesItRandomlyOnBoard() {
        val vm = GameViewModel(initialHand = hand)

        vm.onTileClicked(Tile(3, 5))

        val state = vm.uiState.value
        val placedTilesCount = state.territoryBoard.grid.values.count { it.placedTile != null }
        assertEquals(1, placedTilesCount)
        assertEquals(2, state.players[0].hand.size)
        assertNull(state.selectedTile)
    }

    @Test
    fun randomBoardCreatesCorrectGridDimensions() {
        val board = TerritoryBoard.createRandom(6, 6)
        assertEquals(36, board.grid.size)
    }

    @Test
    fun fortressSquareRequiresTwoHitsToBeControlled() {
        val fortressOneHit = TerritorySquare(row = 0, col = 0, type = SquareType.Fortress, fortressHits = 1)
        val fortressTwoHits = TerritorySquare(row = 0, col = 0, type = SquareType.Fortress, fortressHits = 2)

        assertEquals(false, fortressOneHit.isControlled)
        assertEquals(true, fortressTwoHits.isControlled)
    }

    @Test
    fun normalSquareIsControlledWithOneOwner() {
        val square = TerritorySquare(row = 1, col = 1, type = SquareType.Normal, ownerId = "p1")

        assertEquals(true, square.isControlled)
    }
}