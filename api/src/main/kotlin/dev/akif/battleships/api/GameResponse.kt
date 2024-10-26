package dev.akif.battleships.api

import dev.akif.battleships.api.model.GameStatus
import dev.akif.battleships.api.model.PlacedShip
import dev.akif.battleships.api.model.Player
import java.time.Instant

data class GameResponse(
    val id: Long,
    val boards: Map<Player, List<List<Char>>>,
    val ships: Map<Player, List<PlacedShip>>,
    val status: GameStatus,
    val turn: Player,
    val winner: Player?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val startedAt: Instant?,
    val finishedAt: Instant?
)
