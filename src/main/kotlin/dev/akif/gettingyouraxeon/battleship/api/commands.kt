package dev.akif.gettingyouraxeon.battleship.api

import dev.akif.gettingyouraxeon.battleship.api.model.PlacedShip
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Instant

data class CreateGameCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val timestamp: Instant
)

data class JoinGameCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val player: Player,
    val timestamp: Instant
)

data class PlaceShipCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val player: Player,
    val ship: PlacedShip,
    val timestamp: Instant
)

data class StartGameCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val timestamp: Instant
)

data class ShootCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val player: Player,
    val x: Int,
    val y: Int,
    val timestamp: Instant
)
