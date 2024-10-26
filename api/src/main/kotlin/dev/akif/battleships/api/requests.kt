package dev.akif.battleships.api

import dev.akif.battleships.api.model.Direction
import dev.akif.battleships.api.model.ShipType

data class PlaceShipRequest(
    val x: Int,
    val y: Int,
    val direction: Direction,
    val type: ShipType
)

data class Coordinates(
    val x: Int,
    val y: Int
)
