package dev.akif.battleships.api.model

import dev.akif.battleships.api.InvalidMoveException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.test.assertEquals

class GameTest {
    private val now = Instant.now()

    @Test
    fun `creating a game creates an empty board`() {
        val game = Game.created(1, now)

        assertTrue(game.boards.isEmpty())
        assertEquals(emptyMap(), game.ships)
        assertEquals(GameStatus.Created, game.status)
        assertEquals(now, game.createdAt)
        assertEquals(now, game.updatedAt)
        assertNull(game.startedAt)
        assertNull(game.finishedAt)
    }

    @Nested
    inner class `a player joining the game should` {
        @Test
        fun `fail if the game is not in created state`() {
            val game = Game.created(1, now)
                .copy(status = GameStatus.Started)

            assertThrows<InvalidMoveException> {
                game.playerJoined(Player.A, now)
            }.apply {
                assertEquals("Cannot join game 1 because it is not in created state", reason)
            }
        }

        @Test
        fun `fail if the player has already joined`() {
            val game = Game.created(1, now)
                .playerJoined(Player.A, now)

            assertThrows<InvalidMoveException> {
                game.playerJoined(Player.A, now)
            }.apply {
                assertEquals("Cannot join game 1 because player A has already joined", reason)
            }
        }

        @Test
        fun `add the player to the game`() {
            val game = Game.created(1, now)
                .playerJoined(Player.A, now.plusSeconds(1))

            assertEquals(mapOf(Player.A to emptyList()), game.ships)
            assertEquals(now.plusSeconds(1), game.updatedAt)
        }
    }

    @Nested
    inner class `a player placing a ship should` {
        @Test
        fun `fail if the game is not in created state`() {
            val game = Game.created(1, now)
                .copy(status = GameStatus.Started)

            assertThrows<InvalidMoveException> {
                game.shipPlaced(
                    Player.A,
                    PlacedShip(
                        0,
                        0,
                        Direction.Horizontal,
                        ShipType.Battleship
                    ), now
                )
            }.apply {
                assertEquals("Cannot place ship because game 1 is not in created state", reason)
            }
        }

        @Test
        fun `fail if the ship is already placed`() {
            val game = Game.created(1, now)
                .shipPlaced(
                    Player.A,
                    PlacedShip(
                        0,
                        0,
                        Direction.Horizontal,
                        ShipType.Battleship
                    ), now
                )

            assertThrows<InvalidMoveException> {
                game.shipPlaced(
                    Player.A,
                    PlacedShip(
                        0,
                        0,
                        Direction.Horizontal,
                        ShipType.Battleship
                    ), now
                )
            }.apply {
                assertEquals("Cannot place Battleship for player A because it is already placed", reason)
            }
        }

        @Test
        fun `place the ship on the board`() {
            val ship = PlacedShip(
                0,
                0,
                Direction.Horizontal,
                ShipType.Battleship
            )
            val game = Game.created(1, now)
                .playerJoined(Player.A, now)
                .shipPlaced(Player.A, ship, now.plusSeconds(1))

            assertEquals(Cell.ShipA, game[Player.A, 0, 0])
            assertEquals(Cell.ShipA, game[Player.A, 1, 0])
            assertEquals(Cell.ShipA, game[Player.A, 2, 0])
            assertEquals(Cell.ShipA, game[Player.A, 3, 0])
            assertEquals(listOf(ship), game.ships[Player.A])
            assertEquals(now.plusSeconds(1), game.updatedAt)
        }
    }

    @Nested
    inner class `starting the game should` {
        @Test
        fun `fail if the game is not in created state`() {
            val game = Game.created(1, now)
                .copy(status = GameStatus.Started)

            assertThrows<InvalidMoveException> {
                game.started(now)
            }.apply {
                assertEquals("Cannot start game 1 because it is not in created state", reason)
            }
        }

        @Test
        fun `fail if not all players have placed all of their ships`() {
            val game = Game.created(1, now)
                .playerJoined(Player.A, now)

            assertThrows<InvalidMoveException> {
                game.started(now)
            }.apply {
                assertEquals("Cannot start game because not Player A has not placed all of their ships", reason)
            }
        }

        @Test
        fun `start the game`() {
            val game = Game.created(1, now).copy(
                ships = Player.entries.associateWith {
                    ShipType.entries.map {
                        PlacedShip(
                            0,
                            0,
                            Direction.Horizontal,
                            it
                        )
                    }
                }
            ).started(now.plusSeconds(1))

            assertEquals(GameStatus.Started, game.status)
            assertEquals(Player.A, game.turn)
            assertEquals(now.plusSeconds(1), game.updatedAt)
            assertEquals(now.plusSeconds(1), game.startedAt)
        }
    }

    @Nested
    inner class `a player shooting should` {
        @Nested
        inner class `fail when` {
            @Test
            fun `the game is not started`() {
                val game = Game.created(1, now)

                assertThrows<InvalidMoveException> {
                    game.shot(Player.A, 0, 0, now)
                }.apply {
                    assertEquals("Cannot shoot because game 1 is not started", reason)
                }
            }

            @Test
            fun `it is not player's turn`() {
                val game = Game.created(1, now).copy(
                    status = GameStatus.Started,
                    turn = Player.B
                )

                assertThrows<InvalidMoveException> {
                    game.shot(Player.A, 0, 0, now)
                }.apply {
                    assertEquals("Player A cannot shoot because it is not their turn", reason)
                }
            }

            @Test
            fun `cell has already been shot`() {
                val joinedGame = Game.created(1, now)
                    .playerJoined(Player.A, now)
                val game = joinedGame
                    .copy(
                        status = GameStatus.Started,
                        turn = Player.B,
                        boards = joinedGame.boards + (
                                Player.A to Board()
                                    .shipPlaced(
                                        Player.A,
                                        PlacedShip(
                                            0,
                                            0,
                                            Direction.Horizontal,
                                            ShipType.Carrier
                                        )
                                    )
                                    .shot(Player.B, 0, 0)
                                    .first
                                )
                    )

                assertThrows<InvalidMoveException> {
                    game.shot(Player.B, 0, 0, now)
                }.apply {
                    assertEquals("Player B cannot shoot at (0, 0) because it has already been shot", reason)
                }
            }
        }

        @Test
        fun `hit a ship and keep their turn`() {
            val joinedGame = Game.created(1, now)
                .playerJoined(Player.A, now)
            val game = joinedGame
                .copy(
                    status = GameStatus.Started,
                    turn = Player.B,
                    boards = joinedGame.boards + (
                            Player.A to Board()
                                .shipPlaced(
                                    Player.A,
                                    PlacedShip(
                                        0,
                                        0,
                                        Direction.Horizontal,
                                        ShipType.Carrier
                                    )
                                )
                            )
                )
                .shot(Player.B, 0, 0, now.plusSeconds(1))
                .first

            assertEquals(Cell.Hit, game[Player.A, 0, 0])
            assertEquals(now.plusSeconds(1), game.updatedAt)
            assertEquals(Player.B, game.turn)
        }

        @Test
        fun `miss a ship and lose their turn`() {
            val joinedGame = Game.created(1, now)
                .playerJoined(Player.A, now)
            val game = joinedGame
                .copy(
                    status = GameStatus.Started,
                    turn = Player.B,
                    boards = joinedGame.boards + (
                            Player.A to Board()
                                .shipPlaced(
                                    Player.A,
                                    PlacedShip(
                                        0,
                                        0,
                                        Direction.Horizontal,
                                        ShipType.Carrier
                                    )
                                )
                            )
                )
                .shot(Player.B, 0, 1, now.plusSeconds(1))
                .first

            assertEquals(Cell.Miss, game[Player.A, 0, 1])
            assertEquals(now.plusSeconds(1), game.updatedAt)
            assertEquals(Player.A, game.turn)
        }
    }

    @Nested
    inner class `finishing a game should` {
        @Test
        fun `return the game with the winner`() {
            val game = Game.created(1, now)
                .playerJoined(Player.A, now)
                .playerJoined(Player.B, now)
                .shipPlaced(
                    Player.A,
                    PlacedShip(
                        0,
                        0,
                        Direction.Horizontal,
                        ShipType.PatrolBoat
                    ), now
                )
                .shipPlaced(
                    Player.B,
                    PlacedShip(
                        0,
                        1,
                        Direction.Horizontal,
                        ShipType.PatrolBoat
                    ), now
                )
                .copy(
                    status = GameStatus.Started,
                    turn = Player.B
                )
                .shot(Player.B, 0, 0, now.plusSeconds(1)).first
                .shot(Player.B, 1, 0, now.plusSeconds(2)).first
                .finished(Player.B, now.plusSeconds(3))

            assertNotNull(game)
            assertEquals(GameStatus.Finished, game.status)
            assertEquals(Player.B, game.winner)
            assertEquals(now.plusSeconds(3), game.updatedAt)
            assertEquals(now.plusSeconds(3), game.finishedAt)
        }
    }
}
