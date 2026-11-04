package com.udistrital.taller2_domino.domain.rules

import com.udistrital.taller2_domino.domain.model.Player
import com.udistrital.taller2_domino.domain.model.SquareType
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import com.udistrital.taller2_domino.domain.model.TerritorySquare

object ConquestRules {
    fun processConquest(
        board: TerritoryBoard,
        row: Int,
        col: Int,
        player: Player
    ): Pair<TerritoryBoard, Int>{
        val square=board.squareAt(row, col) ?:return Pair(board,0)
        var pointsGained= 0
        val updateSquare= when (square.type){
            SquareType.Treasure->{
                pointsGained=5
                square.copy(ownerId = player.id)
            }
            SquareType.Bonus->{
                pointsGained=3
                square.copy(ownerId = player.id)
            }
            SquareType.Trap->{
                pointsGained=-2
                square.copy(ownerId = player.id)
            }
            SquareType.Fortress->{
                val newHits=square.fortressHits+1
                if(newHits>= 2){
                    pointsGained=2
                    square.copy(ownerId = player.id,  fortressHits=newHits)
                }else{
                    pointsGained=1
                    square.copy(ownerId = player.id)
                }
            }
            SquareType.Normal->{
                pointsGained=0//casilla sin efecto
                square.copy(ownerId = player.id)
            }

            SquareType.Portal->{
                pointsGained=0//no da punto, solo se mueve de casilla
                square.copy(ownerId = player.id)
            }

        }
        val newGrid=board.grid.toMutableMap()
        newGrid[Pair(row, col)]=updateSquare
        return Pair(board.copy(grid=newGrid),pointsGained)
    }

    fun playerWinner(players:List<Player>, board: TerritoryBoard): Player?{
        if(players.isEmpty()){
            return null
        }
        return players.maxWithOrNull(
            compareBy<Player> { it.score }.thenBy { p->board.grid.values.count{it.ownerId==p.id}}
        )
    }
}