package dev.akif.gettingyouraxeon.battleship.api

import dev.akif.gettingyouraxeon.battleship.api.model.*
import java.time.Instant

data class GameResponse(
    val id: Long,
    val rendered: String,
    val board: List<List<Char>>,
    val ships: Map<Player, List<PlacedShip>>,
    val status: GameStatus,
    val turn: Player,
    val winner: Player?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val startedAt: Instant?,
    val finishedAt: Instant?
)
