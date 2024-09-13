package dev.akif.gettingyouraxeon.battleship.api

import dev.akif.gettingyouraxeon.battleship.api.model.Direction
import dev.akif.gettingyouraxeon.battleship.api.model.ShipType

data class PlaceShipRequest(
    val x: Int,
    val y: Int,
    val direction: Direction,
    val type: ShipType
)
