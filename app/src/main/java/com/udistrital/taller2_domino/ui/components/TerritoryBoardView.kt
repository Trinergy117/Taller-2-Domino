package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    val boardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(6.dp, boardShape)
            .clip(boardShape)
            .background(Color(0xFF1E293B))
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.aspectRatio(1f)) {
            for (r in 0 until board.rows) {
                Row(modifier = Modifier.weight(1f)) {
                    for (c in 0 until board.cols) {
                        val square = board.squareAt(r, c)
                        val owner = players.find { it.id == square?.ownerId }
                        val isValidHighlight = Pair(r, c) in validPlacementSquares
                        val cellShape = RoundedCornerShape(4.dp)

                        val backgroundColor = when {
                            isValidHighlight -> Color(0xFF86EFAC)
                            owner != null -> Color(owner.colorHex).copy(alpha = 0.55f)
                            else -> Color(0xFFF8FAFC)
                        }

                        val borderColor = when {
                            isValidHighlight -> Color(0xFF166534)
                            owner != null -> Color(owner.colorHex)
                            else -> Color(0xFFE2E8F0)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(1.5.dp)
                                .clip(cellShape)
                                .background(backgroundColor)
                                .border(if (isValidHighlight) 2.dp else 1.dp, borderColor, cellShape)
                                .clickable { onSquareClick(r, c) },
                            contentAlignment = Alignment.Center
                        ) {
                            // 1. Dibuja el icono del efecto especial de fondo
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
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.alpha(if (square?.placedTile != null) 0.30f else 0.85f)
                                )
                            }

                            // 2. Dibuja la ficha de dominó sobre la casilla
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
}
