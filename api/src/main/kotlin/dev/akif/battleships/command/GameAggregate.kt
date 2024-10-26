package dev.akif.battleships.command

import dev.akif.battleships.api.*
import dev.akif.battleships.api.model.Game
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.Timestamp
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.time.Instant

@Aggregate
class GameAggregate() {
    @AggregateIdentifier
    private var id: Long? = null
    private lateinit var game: Game

    @CommandHandler
    constructor(command: CreateGameCommand) : this() {
        val event = GameCreatedEvent(command.id)
        applyEvent(event)
    }

    @EventSourcingHandler
    fun on(event: GameCreatedEvent, @Timestamp now: Instant) {
        id = event.id
        game = Game.created(event.id, now)
    }

    @CommandHandler
    fun handle(command: JoinGameCommand) {
        applyEvent(PlayerJoinedEvent(command.id, command.player))
    }

    @EventSourcingHandler
    fun on(event: PlayerJoinedEvent, @Timestamp now: Instant) {
        game = game.playerJoined(event.player, now)
    }

    @CommandHandler
    fun handle(command: PlaceShipCommand) {
        applyEvent(ShipPlacedEvent(command.id, command.player, command.ship))
    }

    @EventSourcingHandler
    fun on(event: ShipPlacedEvent, @Timestamp now: Instant) {
        game = game.shipPlaced(event.player, event.ship, now)
    }

    @CommandHandler
    fun handle(command: StartGameCommand) {
        applyEvent(GameStartedEvent(command.id))
    }

    @EventSourcingHandler
    fun on(event: GameStartedEvent, @Timestamp now: Instant) {
        game = game.started(now)
    }

    @CommandHandler
    fun handle(command: ShootCommand) {
        val (_, winner) = game.shot(command.player, command.x, command.y, Instant.now())
        applyEvent(ShotEvent(command.id, command.player, command.x, command.y))

        if (winner != null) {
            applyEvent(GameFinishedEvent(command.id, winner))
        }
    }

    @EventSourcingHandler
    fun on(event: ShotEvent, @Timestamp now: Instant) {
        val (newGame, _) = game.shot(event.player, event.x, event.y, now)
        game = newGame
    }

    @EventSourcingHandler
    fun on(event: GameFinishedEvent, @Timestamp now: Instant) {
        game = game.finished(event.winner, now)
    }
}
