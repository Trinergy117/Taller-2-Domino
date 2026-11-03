package com.udistrital.taller2_domino.domain.model

data class Player(
    val id: String,
    val name: String,
    val score: Int=0,
    val hand: List<Tile> = emptyList(),
    val colorHex: Long= 0xFF2196F3
)
