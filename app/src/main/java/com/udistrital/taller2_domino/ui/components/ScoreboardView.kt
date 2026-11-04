package com.udistrital.taller2_domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.taller2_domino.domain.model.Player

@Composable
fun ScoreboardView(
    players: List<Player>,
    currentTurnIndex: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val activePlayer = players.getOrNull(currentTurnIndex)
        Text(
            text = "Turno de: ${activePlayer?.name ?: ""}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = activePlayer?.let { Color(it.colorHex) } ?: Color.Black
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            players.forEachIndexed { index, player ->
                val isTurn = index == currentTurnIndex
                val playerColor = Color(player.colorHex)

                Column(
                    modifier = Modifier
                        .background(
                            color = if (isTurn) playerColor.copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = if (isTurn) 2.dp else 1.dp,
                            color = if (isTurn) playerColor else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.SemiBold,
                        color = playerColor
                    )
                    Text(
                        text = "${player.score} pts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}