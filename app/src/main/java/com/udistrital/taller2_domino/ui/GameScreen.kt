package com.udistrital.taller2_domino.ui

import com.udistrital.taller2_domino.ui.game.GameViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udistrital.taller2_domino.domain.model.Side
import com.udistrital.taller2_domino.ui.components.ChainView
import com.udistrital.taller2_domino.ui.components.HandView

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mesa", style = MaterialTheme.typography.titleLarge)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ChainView(chain = state.chain)
        }

        state.message?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.onSideChosen(Side.Left) },
                enabled = Side.Left in state.enabledSides
            ) { Text("← Izquierda") }

            OutlinedButton(
                onClick = { viewModel.onSideChosen(Side.Right) },
                enabled = Side.Right in state.enabledSides
            ) { Text("Derecha →") }
        }

        Spacer(Modifier.height(16.dp))
        Text("Tu mano", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        HandView(
            hand = state.hand,
            playableTiles = state.playableTiles,
            selectedTile = state.selectedTile,
            onTileClick = viewModel::onTileClicked
        )

        TextButton(onClick = viewModel::onNewGame) { Text("Nueva partida") }
    }
}