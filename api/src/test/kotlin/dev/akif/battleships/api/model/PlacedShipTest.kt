package dev.akif.battleships.api.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlacedShipTest {
    @Test
    fun `isSunken should return true if all cells of the ship are hit`() {
        val board = Board()
        val placedShip = PlacedShip(0, 0, Direction.Horizontal, ShipType.Battleship)

        assertFalse(placedShip.isSunken(board))

        board[0, 0] = Cell.Hit
        board[1, 0] = Cell.Hit
        board[2, 0] = Cell.Hit
        board[3, 0] = Cell.Hit

        assertTrue(placedShip.isSunken(board))
    }
}
