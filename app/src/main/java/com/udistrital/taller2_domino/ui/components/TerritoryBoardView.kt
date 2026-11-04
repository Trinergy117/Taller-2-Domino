package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard

@Composable
fun TerritoryBoardView(
    board: TerritoryBoard,
    players: List<Player>,
    validPlacementSquares: Set<Pair<Int, Int>> = emptySet(),
    onSquareClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, Color.DarkGray)
    ) {
        for (r in 0 until board.rows) {
            Row(modifier = Modifier.weight(1f)) {
                for (c in 0 until board.cols) {
                    val square = board.squareAt(r, c)
                    val owner = players.find { it.id == square?.ownerId }
                    val isValidHighlight = Pair(r, c) in validPlacementSquares

                    val backgroundColor = when {
                        isValidHighlight -> Color(0xFFA5D6A7) // Verde claro resaltado
                        owner != null -> Color(owner.colorHex).copy(alpha = 0.45f)
                        else -> Color(0xFFFAFAFA)
                    }

                    val borderColor = when {
                        isValidHighlight -> Color(0xFF2E7D32)
                        else -> Color.LightGray
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(backgroundColor)
                            .border(if (isValidHighlight) 2.dp else 0.5.dp, borderColor)
                            .clickable { onSquareClick(r, c) },
                        contentAlignment = Alignment.Center
                    ) {
                        // 1. Dibuja el icono del efecto especial de fondo (grande y traslúcido)
                        val icon = when (square?.type) {
                            SquareType.Treasure -> "💎"
                            SquareType.Trap -> "💣"
                            SquareType.Bonus -> "🌟"
                            SquareType.Fortress -> if (square.fortressHits > 0) "🏰(${square.fortressHits})" else "🏰"
                            SquareType.Portal -> "🌀"
                            else -> ""
                        }

                        if (icon.isNotEmpty()) {
                            Text(
                                text = icon,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alpha(if (square?.placedTile != null) 0.35f else 0.85f)
                            )
                        }

                        // 2. Dibuja la ficha de dominó auténtica con puntos (pips) sobre la casilla
                        val placed = square?.placedTile
                        if (placed != null) {
                            TileView(
                                left = placed.displayLeft,
                                right = placed.displayRight,
                                faceSize = 13.dp,
                                isVertical = placed.isVertical
                            )
                        }
                    }
                }
            }
        }
    }
}