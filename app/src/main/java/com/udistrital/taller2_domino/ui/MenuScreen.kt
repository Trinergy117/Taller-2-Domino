package com.udistrital.taller2_domino.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    userEmail: String?,
    onNewGame: () -> Unit,
    onOnlineGame: () -> Unit,
    onSavedGames: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Domino", style = MaterialTheme.typography.displayLarge)

        userEmail?.let { email ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sesión: $email",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth()) {
            Text("Nueva Partida (Local)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onOnlineGame, modifier = Modifier.fillMaxWidth()) {
            Text("Partida Online (Multijugador)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = onSavedGames, modifier = Modifier.fillMaxWidth()) {
            Text("Partida Guardada")
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onSignOut) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        userEmail = "jugador@ejemplo.com",
        onNewGame = {},
        onOnlineGame = {},
        onSavedGames = {},
        onSignOut = {}
    )
}
