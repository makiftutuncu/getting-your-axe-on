package dev.akif.battleships.api.model

enum class Cell(val symbol: Char) {
    Empty(' '),
    ShipA('A'),
    ShipB('B'),
    Hit('X'),
    Miss('O')
}
