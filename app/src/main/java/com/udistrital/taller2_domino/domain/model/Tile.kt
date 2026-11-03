package com.udistrital.taller2_domino.domain.model

data class Tile(val low: Int, val high: Int) {
    init {
        require(low in 0..6 && high in low..6) { "Ficha inválida: $low-$high" }
    }

    val pips: Int get() = low + high
    val isDouble: Boolean get() = low == high

    fun has(value: Int): Boolean = low == value || high == value

    // Dado un número que coincide, devuelve el del otro lado
    fun other(value: Int): Int = if (low == value) high else low

    companion object {
        fun of(a: Int, b: Int): Tile = Tile(minOf(a, b), maxOf(a, b))
    }
}