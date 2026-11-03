package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.taller2_domino.domain.model.Chain

@Composable
fun ChainView(chain: Chain, modifier: Modifier = Modifier) {
    if (chain.isEmpty) {
        Text("La mesa está vacía: juega la primera ficha", modifier = modifier)
    } else {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(chain.tiles) { placed ->
                TileView(placed.left, placed.right, faceSize = 28.dp)
            }
        }
    }
}