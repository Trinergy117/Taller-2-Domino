package com.udistrital.taller2_domino

import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.TerritorySquare
import com.udistrital.taller2_domino.domain.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import com.udistrital.taller2_domino.ui.game.GameViewModel


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    //Instancia inicial de mano con fichas para pruebas
    private val hand = listOf(Tile(3, 5), Tile(5, 6), Tile(0, 2))

    @Test
    fun startsWithGivenHandAndEmptyChain() {
        val vm = GameViewModel(initialHand = hand)
        assertEquals(3, vm.uiState.value.hand.size)
        assertEquals(true, vm.uiState.value.chain.isEmpty)
    }

    @Test
    fun playingATileMovesItFromHandToChain() {
        val vm = GameViewModel(initialHand = hand)

        vm.onTileClicked(Tile(3, 5))
        vm.onSideChosen(Side.Right)

        val state = vm.uiState.value
        assertEquals(1, state.chain.tiles.size)
        assertEquals(2, state.hand.size)
        assertNull(state.selectedTile)
    }

    @Test
    fun nonMatchingTileCannotBeSelected() {
        val vm = GameViewModel(initialHand = hand)
        vm.onTileClicked(Tile(3, 5))
        vm.onSideChosen(Side.Right)            // extremos: 3 y 5

        vm.onTileClicked(Tile(0, 2))           // no encaja

        assertNull(vm.uiState.value.selectedTile)
        assertNotNull(vm.uiState.value.message)
    }

    @Test
    fun selectingMatchingTileEnablesOnlyValidSide() {
        val vm = GameViewModel(initialHand = hand)
        vm.onTileClicked(Tile(3, 5))
        vm.onSideChosen(Side.Right)

        vm.onTileClicked(Tile(5, 6))           // el 5 solo coincide a la derecha

        assertEquals(setOf(Side.Right), vm.uiState.value.enabledSides)
    }

    @Test
    fun matchingTileExtendsChain() {
        val vm = GameViewModel(initialHand = hand)
        vm.onTileClicked(Tile(3, 5)); vm.onSideChosen(Side.Right)
        vm.onTileClicked(Tile(5, 6)); vm.onSideChosen(Side.Right)

        assertEquals(6, vm.uiState.value.chain.rightEnd)
    }

    @Test
    fun randomBoardCreatesCorrectGridDimensions() {
        // Generar un tablero de territorio de 6x6
        val board = TerritoryBoard.createRandom(6, 6)
        // Aserción: El mapa debe contener exactamente 36 casillas (6 * 6)
        assertEquals(32, board.grid.size)
    }

    @Test
    fun fortressSquareRequiresTwoHitsToBeControlled() {
        // 1. Creamos una casilla de tipo Fortaleza (Fortress) golpeada 1 sola vez
        val fortressOneHit =
            TerritorySquare(row = 0, col = 0, type = SquareType.Fortress, fortressHits = 1)

        // 2. Creamos una casilla Fortaleza golpeada 2 veces
        val fortressTwoHits = TerritorySquare(row = 0, col = 0, type = SquareType.Fortress, fortressHits = 2)

        // Aserción: Con 1 solo golpe NO debe estar controlada, pero con 2 golpes SÍ.
        assertEquals(false, fortressOneHit.isControlled)
        assertEquals(true, fortressTwoHits.isControlled)
    }

    @Test
    fun normalSquareIsControlledWithOneOwner(){
        val square= TerritorySquare(row = 1, col = 1, type= SquareType.Normal, ownerId = "p1")

        assertEquals(true, square.isControlled)
    }



}