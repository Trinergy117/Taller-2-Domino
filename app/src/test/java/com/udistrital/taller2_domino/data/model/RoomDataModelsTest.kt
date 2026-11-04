package com.udistrital.taller2_domino.data.model

import com.udistrital.taller2_domino.domain.model.PlacedTile
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.TerritorySquare
import com.udistrital.taller2_domino.domain.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RoomDataModelsTest {

    @Test
    fun `test conversion of individual domain models to DTOs and back`() {
        val tile = Tile(3, 5)
        assertEquals(tile, tile.toDto().toDomain())

        val placedTile = PlacedTile(
            tile = Tile(2, 4),
            row = 1,
            col = 3,
            ownerId = "p1",
            displayLeft = 4,
            displayRight = 2,
            isVertical = true
        )
        assertEquals(placedTile, placedTile.toDto().toDomain())

        val square = TerritorySquare(
            row = 2,
            col = 2,
            type = SquareType.Treasure,
            ownerId = "p2",
            fortressHits = 1,
            placedTile = placedTile
        )
        val restoredSquare = square.toDto().toDomain()
        assertEquals(square.row, restoredSquare.row)
        assertEquals(square.col, restoredSquare.col)
        assertEquals(square.type, restoredSquare.type)
        assertEquals(square.ownerId, restoredSquare.ownerId)
        assertEquals(square.fortressHits, restoredSquare.fortressHits)
        assertEquals(square.placedTile, restoredSquare.placedTile)

        val player = Player(
            id = "p1",
            name = "Jugador 1",
            colorHex = 0xFF2196F3,
            score = 15,
            hand = listOf(Tile(0, 0), Tile(1, 4))
        )
        assertEquals(player, player.toDto().toDomain())
    }

    @Test
    fun `test full GameRoomDto and TerritoryBoard grid reconstruction`() {
        val board = TerritoryBoard.createRandom(6, 6)
        val players = listOf(
            Player(id = "p1", name = "Host", score = 10, hand = listOf(Tile(0, 1)), colorHex = 0xFF2196F3),
            Player(id = "p2", name = "Guest", score = 5, hand = listOf(Tile(2, 2)), colorHex = 0xFFE91E63)
        )

        val roomDto = GameRoomDto(
            code = "123456",
            hostId = "p1",
            hostName = "Host",
            guestId = "p2",
            guestName = "Guest",
            status = "PLAYING",
            currentTurnIndex = 0,
            boneyard = listOf(Tile(6, 6).toDto()),
            territoryBoardRows = board.rows,
            territoryBoardCols = board.cols,
            territorySquares = board.toSquareDtoList(),
            players = players.map { it.toDto() }
        )

        val restoredBoard = roomDto.toTerritoryBoard()
        val restoredPlayers = roomDto.players.map { it.toDomain() }

        assertEquals(6, restoredBoard.rows)
        assertEquals(6, restoredBoard.cols)
        assertEquals(36, restoredBoard.grid.size)
        assertNotNull(restoredBoard.squareAt(0, 0))
        assertEquals(players, restoredPlayers)
        assertEquals("PLAYING", roomDto.status)
        assertEquals(1, roomDto.boneyard.size)
    }
}
