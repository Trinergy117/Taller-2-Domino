package com.udistrital.taller2_domino.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MenuScreen(
    onNewGame: () -> Unit,
    onSavedGames: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ){
        Text("Domino", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth()){
            Text("Nueva Partida")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onSavedGames, modifier = Modifier.fillMaxWidth()){
            Text("Partida Guardada")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview(){
    MenuScreen(onNewGame = {}, onSavedGames = {})
}