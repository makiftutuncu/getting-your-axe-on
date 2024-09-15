package dev.akif.gettingyouraxeon.battleship.command

import dev.akif.gettingyouraxeon.battleship.api.*
import dev.akif.gettingyouraxeon.battleship.api.model.*
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Test
import java.time.Instant

class GameAggregateTest {
    private val fixture = AggregateTestFixture(GameAggregate::class.java)
    private val id = 1L
    private val now = Instant.now()

    private operator fun Instant.plus(seconds: Long) = this.plusSeconds(seconds)

    private val game01 =
        Game.created(id, now)
    private val game02 =
        game01.playerJoined(Player.A, now + 1)
    private val game03 =
        game02.playerJoined(Player.B, now + 2)
    private val game04 =
        game03.shipPlaced(Player.A, PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier), now + 3)
    private val game05 =
        game04.shipPlaced(Player.A, PlacedShip(0, 1, Direction.Horizontal, ShipType.Battleship), now + 4)
    private val game06 =
        game05.shipPlaced(Player.A, PlacedShip(0, 2, Direction.Horizontal, ShipType.Destroyer), now + 5)
    private val game07 =
        game06.shipPlaced(Player.A, PlacedShip(0, 3, Direction.Horizontal, ShipType.Submarine), now + 6)
    private val game08 =
        game07.shipPlaced(Player.A, PlacedShip(0, 4, Direction.Horizontal, ShipType.PatrolBoat), now + 7)
    private val game09 =
        game08.shipPlaced(Player.B, PlacedShip(5, 0, Direction.Vertical, ShipType.Carrier), now + 8)
    private val game10 =
        game09.shipPlaced(Player.B, PlacedShip(6, 0, Direction.Vertical, ShipType.Battleship), now + 9)
    private val game11 =
        game10.shipPlaced(Player.B, PlacedShip(7, 0, Direction.Vertical, ShipType.Destroyer), now + 10)
    private val game12 =
        game11.shipPlaced(Player.B, PlacedShip(8, 0, Direction.Vertical, ShipType.Submarine), now + 11)
    private val game13 =
        game12.shipPlaced(Player.B, PlacedShip(9, 0, Direction.Vertical, ShipType.PatrolBoat), now + 12)
    private val game14 =
        game13.started(now + 13)
    private val game15 =
        game14.shot(Player.A, 9, 9, now + 14)
    private val game16 =
        game15.shot(Player.B, 0, 0, now + 15)
    private val game17 =
        game16.shot(Player.B, 1, 0, now + 16)
    private val game18 =
        game17.shot(Player.B, 2, 0, now + 17)
    private val game19 =
        game18.shot(Player.B, 3, 0, now + 18)
    private val game20 =
        game19.shot(Player.B, 4, 0, now + 19)
    private val game21 =
        game20.shot(Player.B, 0, 1, now + 20)
    private val game22 =
        game21.shot(Player.B, 1, 1, now + 21)
    private val game23 =
        game22.shot(Player.B, 2, 1, now + 22)
    private val game24 =
        game23.shot(Player.B, 3, 1, now + 23)
    private val game25 =
        game24.shot(Player.B, 0, 2, now + 24)
    private val game26 =
        game25.shot(Player.B, 1, 2, now + 25)
    private val game27 =
        game26.shot(Player.B, 2, 2, now + 26)
    private val game28 =
        game27.shot(Player.B, 0, 3, now + 27)
    private val game29 =
        game28.shot(Player.B, 1, 3, now + 28)
    private val game30 =
        game29.shot(Player.B, 2, 3, now + 29)
    private val game31 =
        game30.shot(Player.B, 0, 4, now + 30)
    private val game32 =
        game31.shot(Player.B, 1, 4, now + 31)

    @Test
    fun `handling CreateGameCommand should create a new game`() {
        fixture
            .givenNoPriorActivity()
            .`when`(CreateGameCommand(id, now))
            .expectEvents(GameCreatedEvent(id, now))
    }

    @Test
    fun `handling JoinGameCommand should add a player to the game`() {
        fixture
            .given(GameCreatedEvent(id, now))
            .`when`(JoinGameCommand(id, Player.A, now + 1))
            .expectEvents(PlayerJoinedEvent(game02))
    }

    @Test
    fun `handling PlaceShipCommand should place a ship to the game`() {
        fixture
            .given(
                GameCreatedEvent(id, now),
                PlayerJoinedEvent(game02),
                PlayerJoinedEvent(game03),
            )
            .`when`(
                PlaceShipCommand(
                    id,
                    Player.A,
                    PlacedShip(0, 0, Direction.Horizontal, ShipType.Carrier),
                    now + 3
                )
            )
            .expectEvents(ShipPlacedEvent(game04))
    }

    @Test
    fun `handling StartGameCommand should start the game`() {
        fixture
            .given(
                GameCreatedEvent(id, now),
                PlayerJoinedEvent(game02),
                PlayerJoinedEvent(game03),
                ShipPlacedEvent(game04),
                ShipPlacedEvent(game05),
                ShipPlacedEvent(game06),
                ShipPlacedEvent(game07),
                ShipPlacedEvent(game08),
                ShipPlacedEvent(game09),
                ShipPlacedEvent(game10),
                ShipPlacedEvent(game11),
                ShipPlacedEvent(game12),
                ShipPlacedEvent(game13),
            )
            .`when`(StartGameCommand(id, now + 13))
            .expectEvents(GameStartedEvent(game14))
    }

    @Test
    fun `handling ShootCommand should make a shot in the game`() {
        fixture
            .given(
                GameCreatedEvent(id, now),
                PlayerJoinedEvent(game02),
                PlayerJoinedEvent(game03),
                ShipPlacedEvent(game04),
                ShipPlacedEvent(game05),
                ShipPlacedEvent(game06),
                ShipPlacedEvent(game07),
                ShipPlacedEvent(game08),
                ShipPlacedEvent(game09),
                ShipPlacedEvent(game10),
                ShipPlacedEvent(game11),
                ShipPlacedEvent(game12),
                ShipPlacedEvent(game13),
                GameStartedEvent(game14)
            )
            .`when`(ShootCommand(id, Player.A, 9, 9, now + 14))
            .expectEvents(ShotEvent(game15))
    }

    @Test
    fun `handling ShootCommand should finish the game`() {
        fixture
            .given(
                GameCreatedEvent(id, now),
                PlayerJoinedEvent(game02),
                PlayerJoinedEvent(game03),
                ShipPlacedEvent(game04),
                ShipPlacedEvent(game05),
                ShipPlacedEvent(game06),
                ShipPlacedEvent(game07),
                ShipPlacedEvent(game08),
                ShipPlacedEvent(game09),
                ShipPlacedEvent(game10),
                ShipPlacedEvent(game11),
                ShipPlacedEvent(game12),
                ShipPlacedEvent(game13),
                GameStartedEvent(game14),
                ShotEvent(game15),
                ShotEvent(game16),
                ShotEvent(game17),
                ShotEvent(game18),
                ShotEvent(game19),
                ShotEvent(game20),
                ShotEvent(game21),
                ShotEvent(game22),
                ShotEvent(game23),
                ShotEvent(game24),
                ShotEvent(game25),
                ShotEvent(game26),
                ShotEvent(game27),
                ShotEvent(game28),
                ShotEvent(game29),
                ShotEvent(game30),
                ShotEvent(game31),
            )
            .`when`(ShootCommand(id, Player.B, 1, 4, now + 31))
            .expectEvents(
                ShotEvent(game32),
                GameFinishedEvent(
                    requireNotNull(game32.getFinishedGame(now + 31)) {
                        "Game should be finished"
                    }
                )
            )
    }
}
