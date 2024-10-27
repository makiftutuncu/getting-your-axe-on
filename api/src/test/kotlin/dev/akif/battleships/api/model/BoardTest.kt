package dev.akif.battleships.api.model

import dev.akif.battleships.api.InvalidMoveException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BoardTest {
    @Nested
    inner class `creating a board` {
        @Test
        fun `creates a 10x10 board with all cells empty`() {
            val board = Board()

            for (y in 0 until Board.SIZE) {
                for (x in 0 until Board.SIZE) {
                    assertEquals(Cell.Empty, board[x, y])
                }
            }
        }
    }

    @Nested
    inner class `placing a ship on a board` {
        @Nested
        inner class `fails when` {
            @Test
            fun `x coordinate is invalid`() {
                val board = Board()

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(-1, 0, Direction.Horizontal, ShipType.Carrier))
                }.apply {
                    assertEquals("Invalid x coordinate: -1", reason)
                }

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(10, 0, Direction.Horizontal, ShipType.Carrier))
                }.apply {
                    assertEquals("Invalid x coordinate: 10", reason)
                }
            }

            @Test
            fun `y coordinate is invalid`() {
                val board = Board()

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(0, -1, Direction.Horizontal, ShipType.Carrier))
                }.apply {
                    assertEquals("Invalid y coordinate: -1", reason)
                }

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(0, 10, Direction.Horizontal, ShipType.Carrier))
                }.apply {
                    assertEquals("Invalid y coordinate: 10", reason)
                }
            }

            @Test
            fun `ship does not fit horizontally`() {
                val board = Board()

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(9, 0, Direction.Horizontal, ShipType.Carrier))
                }.apply {
                    assertEquals("Cannot place Carrier horizontally at (9, 0) because it does not fit", reason)
                }
            }

            @Test
            fun `placing ship horizontally if coordinates are already occupied`() {
                val board = Board().shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(2, 0, Direction.Horizontal, ShipType.Carrier))
                }.apply {
                    assertEquals("Cannot place Carrier horizontally at (2, 0) because it is already occupied", reason)
                }
            }

            @Test
            fun `ship does not fit vertically`() {
                val board = Board()

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(0, 9, Direction.Vertical, ShipType.Carrier))
                }.apply {
                    assertEquals("Cannot place Carrier vertically at (0, 9) because it does not fit", reason)
                }
            }

            @Test
            fun `placing ship vertically if coordinates are already occupied`() {
                val board = Board().shipPlaced(Player.A, PlacedShip(0, 0, Direction.Vertical, ShipType.Carrier))

                assertThrows(InvalidMoveException::class.java) {
                    board.shipPlaced(Player.A, PlacedShip(0, 2, Direction.Vertical, ShipType.Carrier))
                }.apply {
                    assertEquals("Cannot place Carrier vertically at (0, 2) because it is already occupied", reason)
                }
            }
        }

        @Test
        fun `places a ship horizontally on a board`() {
            val board = Board().shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))

            for (x in 0 until ShipType.Carrier.size) {
                assertEquals(Cell.ShipA, board[x, 0])
            }

            for (y in 1 until Board.SIZE) {
                for (x in 0 until Board.SIZE) {
                    assertEquals(Cell.Empty, board[x, y])
                }
            }
        }

        @Test
        fun `places a ship vertically on a board`() {
            val board = Board().shipPlaced(Player.A, PlacedShip(0, 0, Direction.Vertical, ShipType.Carrier))

            for (y in 0 until ShipType.Carrier.size) {
                assertEquals(Cell.ShipA, board[0, y])
            }

            for (y in ShipType.Carrier.size until Board.SIZE) {
                for (x in 0 until Board.SIZE) {
                    assertEquals(Cell.Empty, board[x, y])
                }
            }
        }
    }

    @Nested
    inner class `shooting on a board` {
        @Test
        fun `fails when shooting at an already shot cell`() {
            val (board, _) = Board().shot(Player.A, 0, 0)

            assertThrows(InvalidMoveException::class.java) {
                board.shot(Player.A, 0, 0)
            }.apply {
                assertEquals("Player A cannot shoot at (0, 0) because it has already been shot", reason)
            }
        }

        @Test
        fun `fails when shooting at a cell with player's own ship`() {
            val board = Board().shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))

            assertThrows(InvalidMoveException::class.java) {
                board.shot(Player.A, 0, 0)
            }.apply {
                assertEquals("Player A cannot shoot at (0, 0) because they have a ship there", reason)
            }
        }

        @Test
        fun `hits a ship on a board`() {
            val (board, cell) = Board()
                .shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))
                .shot(Player.B, 0, 0)

            assertEquals(Cell.Hit, cell)

            for (x in 0 until Board.SIZE) {
                for (y in 0 until Board.SIZE) {
                    if (x == 0 && y == 0) {
                        assertEquals(Cell.Hit, board[x, y])
                    } else {
                        assertNotEquals(Cell.Hit, board[x, y])
                    }
                }
            }
        }

        @Test
        fun `misses a ship on a board`() {
            val (board, cell) = Board()
                .shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))
                .shot(Player.B, 0, 1)

            assertEquals(Cell.Miss, cell)

            for (x in 0 until Board.SIZE) {
                for (y in 0 until Board.SIZE) {
                    if (x == 0 && y == 1) {
                        assertEquals(Cell.Miss, board[x, y])
                    } else {
                        assertNotEquals(Cell.Miss, board[x, y])
                    }
                }
            }
        }
    }

    @Test
    fun `returns a string representation of the board`() {
        val board = Board()
            .shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))
            .shipPlaced(Player.B, PlacedShip(1, 1, Direction.Vertical, ShipType.Battleship))
            .shot(Player.B, 0, 0)
            .first
            .shot(Player.A, 1, 2)
            .first
            .shot(Player.A, 2, 2)
            .first

        assertEquals(
            """
            |  0 1 2 3 4 5 6 7 8 9
            |0 X A A A A          
            |1   B                
            |2   X O              
            |3   B                
            |4   B                
            |5                    
            |6                    
            |7                    
            |8                    
            |9                    
            """.trimMargin(),
            board.toString()
        )
    }
}
