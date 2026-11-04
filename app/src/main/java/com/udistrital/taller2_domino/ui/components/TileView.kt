package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.shadow
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
private fun Face(value: Int, pipColor: Color = Color(0xFF0F172A), modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cell = size.minDimension / 3f
        val radius = cell * 0.28f
        PIPS.getValue(value).forEach { (col, row) ->
            drawCircle(
                color = pipColor,
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
    faceSize: Dp = 32.dp,
    borderColor: Color = Color(0xFF64748B),
    isVertical: Boolean = false,
    tileColor: Color = Color(0xFFFFFBEA),
    pipColor: Color = Color(0xFF0F172A)
) {
    val shape = RoundedCornerShape(6.dp)
    val borderWidth = if (borderColor == Color(0xFFF9A825) || borderColor == Color(0xFF2E7D32)) 2.dp else 1.dp

    if (isVertical) {
        Column(
            modifier = modifier
                .shadow(2.dp, shape)
                .clip(shape)
                .background(tileColor)
                .border(borderWidth, borderColor, shape)
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Face(left, pipColor, Modifier.size(faceSize))
            Box(
                modifier = Modifier
                    .width(faceSize * 0.85f)
                    .height(1.5.dp)
                    .background(Color(0xFFD97706))
            )
            Face(right, pipColor, Modifier.size(faceSize))
        }
    } else {
        Row(
            modifier = modifier
                .shadow(2.dp, shape)
                .clip(shape)
                .background(tileColor)
                .border(borderWidth, borderColor, shape)
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Face(left, pipColor, Modifier.size(faceSize))
            Box(
                modifier = Modifier
                    .width(1.5.dp)
                    .height(faceSize * 0.85f)
                    .background(Color(0xFFD97706))
            )
            Face(right, pipColor, Modifier.size(faceSize))
        }
    }
}
