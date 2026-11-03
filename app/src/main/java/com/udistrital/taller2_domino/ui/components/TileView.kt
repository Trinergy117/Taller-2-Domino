package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Posiciones de los puntos en una cuadrícula de 3x3 (columna, fila)
private val PIPS: Map<Int, List<Pair<Int, Int>>> = mapOf(
    0 to emptyList(),
    1 to listOf(1 to 1),
    2 to listOf(0 to 0, 2 to 2),
    3 to listOf(0 to 0, 1 to 1, 2 to 2),
    4 to listOf(0 to 0, 2 to 0, 0 to 2, 2 to 2),
    5 to listOf(0 to 0, 2 to 0, 1 to 1, 0 to 2, 2 to 2),
    6 to listOf(0 to 0, 2 to 0, 0 to 1, 2 to 1, 0 to 2, 2 to 2)
)

@Composable
private fun Face(value: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cell = size.minDimension / 3f
        val radius = cell * 0.28f
        PIPS.getValue(value).forEach { (col, row) ->
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = Offset(cell * (col + 0.5f), cell * (row + 0.5f))
            )
        }
    }
}

@Composable
fun TileView(
    left: Int,
    right: Int,
    modifier: Modifier = Modifier,
    faceSize: Dp = 44.dp,
    borderColor: Color = Color.DarkGray
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFFFFFBEA))
            .border(2.dp, borderColor, shape)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Face(left, Modifier.size(faceSize))
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(faceSize * 0.8f)
                .background(Color.Gray)
        )
        Face(right, Modifier.size(faceSize))
    }
}