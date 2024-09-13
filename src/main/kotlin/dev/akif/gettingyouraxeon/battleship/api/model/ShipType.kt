package dev.akif.gettingyouraxeon.battleship.api.model

enum class ShipType(val size: Int) {
    Carrier(5),
    Battleship(4),
    Destroyer(3),
    Submarine(3),
    PatrolBoat(2)
}
