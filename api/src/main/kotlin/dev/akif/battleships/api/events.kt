package dev.akif.battleships.api

import dev.akif.battleships.api.model.PlacedShip
import dev.akif.battleships.api.model.Player

interface GameEvent {
    val id: Long
}

data class GameCreatedEvent(
    override val id: Long,
): GameEvent

data class PlayerJoinedEvent(
    override val id: Long,
    val player: Player,
): GameEvent

data class ShipPlacedEvent(
    override val id: Long,
    val player: Player,
    val ship: PlacedShip
): GameEvent

data class GameStartedEvent(
    override val id: Long,
): GameEvent

data class ShotEvent(
    override val id: Long,
    val player: Player,
    val x: Int,
    val y: Int
): GameEvent

data class GameFinishedEvent(
    override val id: Long,
    val winner: Player
): GameEvent
