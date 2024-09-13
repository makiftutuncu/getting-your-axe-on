package dev.akif.gettingyouraxeon.battleship.api

import dev.akif.gettingyouraxeon.battleship.api.model.Game
import java.time.Instant

data class GameCreatedEvent(
    val id: Long,
    val timestamp: Instant
)

data class PlayerJoinedEvent(
    val newGame: Game
)

data class ShipPlacedEvent(
    val newGame: Game
)

data class GameStartedEvent(
    val newGame: Game
)

data class ShotEvent(
    val newGame: Game
)

data class GameFinishedEvent(
    val newGame: Game
)
