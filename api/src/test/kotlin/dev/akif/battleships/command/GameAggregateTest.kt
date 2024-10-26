package dev.akif.battleships.command

import dev.akif.battleships.api.*
import dev.akif.battleships.api.model.Direction
import dev.akif.battleships.api.model.PlacedShip
import dev.akif.battleships.api.model.Player
import dev.akif.battleships.api.model.ShipType
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Test

class GameAggregateTest {
    private val fixture = AggregateTestFixture(GameAggregate::class.java)
    private val id = 1L

    @Test
    fun `handling CreateGameCommand should create a new game`() {
        fixture
            .givenNoPriorActivity()
            .`when`(CreateGameCommand(id))
            .expectEvents(GameCreatedEvent(id))
    }

    @Test
    fun `handling JoinGameCommand should add a player to the game`() {
        fixture
            .given(GameCreatedEvent(id))
            .`when`(JoinGameCommand(id, Player.A))
            .expectEvents(PlayerJoinedEvent(id, Player.A))
    }

    @Test
    fun `handling PlaceShipCommand should place a ship to the game`() {
        fixture
            .given(
                GameCreatedEvent(id),
                PlayerJoinedEvent(id, Player.A),
                PlayerJoinedEvent(id, Player.B),
            )
            .`when`(
                PlaceShipCommand(
                    id,
                    Player.A,
                    PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier)
                )
            )
            .expectEvents(
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier))
            )
    }

    @Test
    fun `handling StartGameCommand should start the game`() {
        fixture
            .given(
                GameCreatedEvent(id),
                PlayerJoinedEvent(id, Player.A),
                PlayerJoinedEvent(id, Player.B),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 1, Direction.Horizontal, ShipType.Battleship)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 2, Direction.Horizontal, ShipType.Destroyer)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 3, Direction.Horizontal, ShipType.Submarine)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 4, Direction.Horizontal, ShipType.PatrolBoat)),
                ShipPlacedEvent(id, Player.B, PlacedShip(5, 0, Direction.Vertical, ShipType.Carrier)),
                ShipPlacedEvent(id, Player.B, PlacedShip(6, 0, Direction.Vertical, ShipType.Battleship)),
                ShipPlacedEvent(id, Player.B, PlacedShip(7, 0, Direction.Vertical, ShipType.Destroyer)),
                ShipPlacedEvent(id, Player.B, PlacedShip(8, 0, Direction.Vertical, ShipType.Submarine)),
                ShipPlacedEvent(id, Player.B, PlacedShip(9, 0, Direction.Vertical, ShipType.PatrolBoat))
            )
            .`when`(StartGameCommand(id))
            .expectEvents(GameStartedEvent(id))
    }

    @Test
    fun `handling ShootCommand should make a shot in the game`() {
        fixture
            .given(
                GameCreatedEvent(id),
                PlayerJoinedEvent(id, Player.A),
                PlayerJoinedEvent(id, Player.B),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 1, Direction.Horizontal, ShipType.Battleship)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 2, Direction.Horizontal, ShipType.Destroyer)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 3, Direction.Horizontal, ShipType.Submarine)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 4, Direction.Horizontal, ShipType.PatrolBoat)),
                ShipPlacedEvent(id, Player.B, PlacedShip(5, 0, Direction.Vertical, ShipType.Carrier)),
                ShipPlacedEvent(id, Player.B, PlacedShip(6, 0, Direction.Vertical, ShipType.Battleship)),
                ShipPlacedEvent(id, Player.B, PlacedShip(7, 0, Direction.Vertical, ShipType.Destroyer)),
                ShipPlacedEvent(id, Player.B, PlacedShip(8, 0, Direction.Vertical, ShipType.Submarine)),
                ShipPlacedEvent(id, Player.B, PlacedShip(9, 0, Direction.Vertical, ShipType.PatrolBoat)),
                GameStartedEvent(id)
            )
            .`when`(ShootCommand(id, Player.A, 9, 9))
            .expectEvents(ShotEvent(id, Player.A, 9, 9))
    }

    @Test
    fun `handling ShootCommand should finish the game`() {
        fixture
            .given(
                GameCreatedEvent(id),
                PlayerJoinedEvent(id, Player.A),
                PlayerJoinedEvent(id, Player.B),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 1, Direction.Horizontal, ShipType.Battleship)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 2, Direction.Horizontal, ShipType.Destroyer)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 3, Direction.Horizontal, ShipType.Submarine)),
                ShipPlacedEvent(id, Player.A, PlacedShip(0, 4, Direction.Horizontal, ShipType.PatrolBoat)),
                ShipPlacedEvent(id, Player.B, PlacedShip(5, 0, Direction.Vertical, ShipType.Carrier)),
                ShipPlacedEvent(id, Player.B, PlacedShip(6, 0, Direction.Vertical, ShipType.Battleship)),
                ShipPlacedEvent(id, Player.B, PlacedShip(7, 0, Direction.Vertical, ShipType.Destroyer)),
                ShipPlacedEvent(id, Player.B, PlacedShip(8, 0, Direction.Vertical, ShipType.Submarine)),
                ShipPlacedEvent(id, Player.B, PlacedShip(9, 0, Direction.Vertical, ShipType.PatrolBoat)),
                GameStartedEvent(id),
                ShotEvent(id, Player.A, 9, 9),
                ShotEvent(id, Player.B, 0, 0),
                ShotEvent(id, Player.B, 1, 0),
                ShotEvent(id, Player.B, 2, 0),
                ShotEvent(id, Player.B, 3, 0),
                ShotEvent(id, Player.B, 4, 0),
                ShotEvent(id, Player.B, 0, 1),
                ShotEvent(id, Player.B, 1, 1),
                ShotEvent(id, Player.B, 2, 1),
                ShotEvent(id, Player.B, 3, 1),
                ShotEvent(id, Player.B, 0, 2),
                ShotEvent(id, Player.B, 1, 2),
                ShotEvent(id, Player.B, 2, 2),
                ShotEvent(id, Player.B, 0, 3),
                ShotEvent(id, Player.B, 1, 3),
                ShotEvent(id, Player.B, 2, 3),
                ShotEvent(id, Player.B, 0, 4)
            )
            .`when`(ShootCommand(id, Player.B, 1, 4))
            .expectEvents(
                ShotEvent(id, Player.B, 1, 4),
                GameFinishedEvent(id, Player.B)
            )
    }
}
