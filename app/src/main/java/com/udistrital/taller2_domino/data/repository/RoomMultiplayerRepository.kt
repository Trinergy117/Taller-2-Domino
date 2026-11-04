package com.udistrital.taller2_domino.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.udistrital.taller2_domino.data.model.GameRoomDto
import com.udistrital.taller2_domino.data.model.PlayerDto
import com.udistrital.taller2_domino.data.model.toDto
import com.udistrital.taller2_domino.data.model.toSquareDtoList
import com.udistrital.taller2_domino.domain.model.DominoSet
import com.udistrital.taller2_domino.domain.model.TerritoryBoard
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface MultiplayerRepository {
    suspend fun createRoom(hostId: String, hostName: String): Result<String>
    suspend fun joinRoom(code: String, guestId: String, guestName: String): Result<GameRoomDto>
    fun listenToRoom(code: String): Flow<GameRoomDto?>
    suspend fun updateGameState(code: String, updatedRoom: GameRoomDto): Result<Unit>
}

class FirebaseMultiplayerRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : MultiplayerRepository {

    override suspend fun createRoom(hostId: String, hostName: String): Result<String> {
        return runCatching {
            val code = (100000..999999).random().toString()
            val fullSet = DominoSet.full().shuffled()
            val p1Hand = fullSet.take(7).map { it.toDto() }
            val p2Hand = fullSet.drop(7).take(7).map { it.toDto() }
            val boneyard = fullSet.drop(14).map { it.toDto() }

            val p1 = PlayerDto(
                id = hostId,
                name = hostName,
                colorHex = 0xFF2196F3,
                score = 0,
                hand = p1Hand
            )
            val p2 = PlayerDto(
                id = "",
                name = "Esperando rival...",
                colorHex = 0xFFE91E63,
                score = 0,
                hand = p2Hand
            )

            val initialBoard = TerritoryBoard.createRandom(6, 6)

            val roomDto = GameRoomDto(
                code = code,
                hostId = hostId,
                hostName = hostName,
                guestId = null,
                guestName = null,
                status = "WAITING",
                currentTurnIndex = 0,
                boneyard = boneyard,
                territoryBoardRows = initialBoard.rows,
                territoryBoardCols = initialBoard.cols,
                territorySquares = initialBoard.toSquareDtoList(),
                players = listOf(p1, p2),
                winnerId = null
            )

            suspendCancellableCoroutine { continuation ->
                db.collection("rooms").document(code)
                    .set(roomDto)
                    .addOnSuccessListener {
                        if (continuation.isActive) continuation.resume(code)
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) continuation.resumeWithException(exception)
                    }
            }
        }
    }

    override suspend fun joinRoom(code: String, guestId: String, guestName: String): Result<GameRoomDto> {
        return runCatching {
            suspendCancellableCoroutine { continuation ->
                val docRef = db.collection("rooms").document(code)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val room = document.toObject(GameRoomDto::class.java)
                            if (room == null) {
                                if (continuation.isActive) continuation.resumeWithException(IllegalStateException("No se pudo parsear la sala"))
                                return@addOnSuccessListener
                            }
                            if (room.status != "WAITING") {
                                if (continuation.isActive) continuation.resumeWithException(IllegalStateException("La sala no está disponible o ya inició"))
                                return@addOnSuccessListener
                            }

                            val updatedPlayers = room.players.toMutableList()
                            if (updatedPlayers.size >= 2) {
                                updatedPlayers[1] = updatedPlayers[1].copy(
                                    id = guestId,
                                    name = guestName
                                )
                            }

                            val updatedRoom = room.copy(
                                guestId = guestId,
                                guestName = guestName,
                                status = "PLAYING",
                                players = updatedPlayers
                            )

                            docRef.set(updatedRoom)
                                .addOnSuccessListener {
                                    if (continuation.isActive) continuation.resume(updatedRoom)
                                }
                                .addOnFailureListener { e ->
                                    if (continuation.isActive) continuation.resumeWithException(e)
                                }
                        } else {
                            if (continuation.isActive) continuation.resumeWithException(IllegalArgumentException("Sala no encontrada"))
                        }
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resumeWithException(e)
                    }
            }
        }
    }

    override fun listenToRoom(code: String): Flow<GameRoomDto?> = callbackFlow {
        val listenerRegistration = db.collection("rooms").document(code)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val room = snapshot.toObject(GameRoomDto::class.java)
                    trySend(room)
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun updateGameState(code: String, updatedRoom: GameRoomDto): Result<Unit> {
        return runCatching {
            suspendCancellableCoroutine { continuation ->
                db.collection("rooms").document(code)
                    .set(updatedRoom)
                    .addOnSuccessListener {
                        if (continuation.isActive) continuation.resume(Unit)
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resumeWithException(e)
                    }
            }
        }
    }
}
