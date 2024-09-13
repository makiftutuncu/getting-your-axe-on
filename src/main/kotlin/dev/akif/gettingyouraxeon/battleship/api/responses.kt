package dev.akif.gettingyouraxeon.battleship.api

import dev.akif.gettingyouraxeon.battleship.api.model.Board
import dev.akif.gettingyouraxeon.battleship.api.model.GameStatus
import dev.akif.gettingyouraxeon.battleship.api.model.PlacedShip
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import java.time.Instant

data class GameResponse(
    val rendered: String,
    val board: Board,
    val ships: Map<Player, List<PlacedShip>>,
    val status: GameStatus,
    val turn: Player,
    val winner: Player?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val startedAt: Instant?,
    val finishedAt: Instant?
)
