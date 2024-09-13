package dev.akif.gettingyouraxeon.battleship.api.model

enum class Cell(val symbol: Char) {
    Empty(' '),
    ShipA('A'),
    ShipB('B'),
    Hit('X'),
    Miss('O')
}
