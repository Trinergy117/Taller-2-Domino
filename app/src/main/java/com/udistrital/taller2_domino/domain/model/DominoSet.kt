package com.udistrital.taller2_domino.domain.model

object DominoSet {
    fun full(): List<Tile> = buildList {
        for (a in 0..6) {
            for (b in a..6) add(Tile(a, b))
        }
    }
}