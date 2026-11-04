package com.udistrital.taller2_domino.data.model

import com.udistrital.taller2_domino.domain.model.PlacedTile
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.TerritorySquare
import com.udistrital.taller2_domino.domain.model.Tile

data class TileDto(
    val low: Int = 0,
    val high: Int = 0
)

fun Tile.toDto() = TileDto(low = low, high = high)
fun TileDto.toDomain() = Tile(low = low, high = high)

data class PlacedTileDto(
    val low: Int = 0,
    val high: Int = 0,
    val row: Int = 0,
    val col: Int = 0,
    val ownerId: String? = null,
    val displayLeft: Int = 0,
    val displayRight: Int = 0,
    val isVertical: Boolean = false
)

fun PlacedTile.toDto() = PlacedTileDto(
    low = tile.low,
    high = tile.high,
    row = row,
    col = col,
    ownerId = ownerId,
    displayLeft = displayLeft,
    displayRight = displayRight,
    isVertical = isVertical
)

fun PlacedTileDto.toDomain() = PlacedTile(
    tile = Tile(low = low, high = high),
    row = row,
    col = col,
    ownerId = ownerId ?: "",
    displayLeft = displayLeft,
    displayRight = displayRight,
    isVertical = isVertical
)

data class TerritorySquareDto(
    val row: Int = 0,
    val col: Int = 0,
    val type: String = "Normal",
    val ownerId: String? = null,
    val fortressHits: Int = 0,
    val placedTile: PlacedTileDto? = null
)

fun TerritorySquare.toDto() = TerritorySquareDto(
    row = row,
    col = col,
    type = type.name,
    ownerId = ownerId,
    fortressHits = fortressHits,
    placedTile = placedTile?.toDto()
)

fun TerritorySquareDto.toDomain() = TerritorySquare(
    row = row,
    col = col,
    type = runCatching { SquareType.valueOf(type) }.getOrDefault(SquareType.Normal),
    ownerId = ownerId,
    fortressHits = fortressHits,
    placedTile = placedTile?.toDomain()
)

data class PlayerDto(
    val id: String = "",
    val name: String = "",
    val colorHex: Long = 0xFF2196F3,
    val score: Int = 0,
    val hand: List<TileDto> = emptyList()
)

fun Player.toDto() = PlayerDto(
    id = id,
    name = name,
    colorHex = colorHex,
    score = score,
    hand = hand.map { it.toDto() }
)

fun PlayerDto.toDomain() = Player(
    id = id,
    name = name,
    colorHex = colorHex,
    score = score,
    hand = hand.map { it.toDomain() }
)

data class GameRoomDto(
    val code: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val guestId: String? = null,
    val guestName: String? = null,
    val status: String = "WAITING", // "WAITING", "PLAYING", "FINISHED"
    val currentTurnIndex: Int = 0,
    val boneyard: List<TileDto> = emptyList(),
    val territoryBoardRows: Int = 6,
    val territoryBoardCols: Int = 6,
    val territorySquares: List<TerritorySquareDto> = emptyList(),
    val players: List<PlayerDto> = emptyList(),
    val winnerId: String? = null,
    val restartRequestedBy: String? = null
) {
    fun toTerritoryBoard(): TerritoryBoard {
        val gridMap = territorySquares.associate { squareDto ->
            Pair(squareDto.row, squareDto.col) to squareDto.toDomain()
        }
        return TerritoryBoard(
            rows = territoryBoardRows,
            cols = territoryBoardCols,
            grid = gridMap
        )
    }
}

fun TerritoryBoard.toSquareDtoList(): List<TerritorySquareDto> {
    return grid.values.map { it.toDto() }
}
