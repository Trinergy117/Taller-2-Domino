package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.udistrital.taller2_domino.domain.model.Tile

@Composable
fun HandView(
    hand: List<Tile>,
    playableTiles: Set<Tile>,
    selectedTile: Tile?,
    onTileClick: (Tile) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hand, key = { it.low * 10 + it.high }) { tile ->
            val borderColor = when {
                tile == selectedTile -> Color(0xFFF9A825)   // amarillo: seleccionada
                tile in playableTiles -> Color(0xFF2E7D32)  // verde: se puede jugar
                else -> Color.Gray
            }
            TileView(
                left = tile.low,
                right = tile.high,
                faceSize = 32.dp,
                borderColor = borderColor,
                modifier = Modifier
                    .alpha(if (tile in playableTiles) 1f else 0.5f)
                    .clickable { onTileClick(tile) }
            )
        }
    }
}