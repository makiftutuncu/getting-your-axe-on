package dev.akif.battleships.api

import dev.akif.battleships.api.model.PlacedShip
import dev.akif.battleships.api.model.Player
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateGameCommand(
    @TargetAggregateIdentifier
    val id: Long
)

data class JoinGameCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val player: Player
)

data class PlaceShipCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val player: Player,
    val ship: PlacedShip
)

data class StartGameCommand(
    @TargetAggregateIdentifier
    val id: Long
)

data class ShootCommand(
    @TargetAggregateIdentifier
    val id: Long,
    val player: Player,
    val x: Int,
    val y: Int
)
