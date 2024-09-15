package dev.akif.gettingyouraxeon.battleship.api.model

data class PlacedShip(
    val x: Int,
    val y: Int,
    val direction: Direction,
    val type: ShipType
) {
    fun isSunken(board: Board): Boolean =
        when (direction) {
            Direction.Horizontal -> (x until (x + type.size)).all { board[it, y] == Cell.Hit }
            Direction.Vertical -> (y until (y + type.size)).all { board[x, it] == Cell.Hit }
        }
}
