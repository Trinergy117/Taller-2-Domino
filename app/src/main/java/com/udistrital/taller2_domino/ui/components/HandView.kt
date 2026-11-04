package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
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
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(hand, key = { it.low * 10 + it.high }) { tile ->
            val isSelected = tile == selectedTile
            val isPlayable = tile in playableTiles

            val borderColor = when {
                isSelected -> Color(0xFFD97706)   // Dorado intenso
                isPlayable -> Color(0xFF059669)   // Verde esmeralda intenso
                else -> Color(0xFF94A3B8)
            }

            TileView(
                left = tile.low,
                right = tile.high,
                faceSize = 32.dp,
                borderColor = borderColor,
                modifier = Modifier
                    .alpha(if (isPlayable) 1f else 0.45f)
                    .clickable { onTileClick(tile) }
            )
        }
    }
}
