package dev.akif.gettingyouraxeon.battleship.command

import dev.akif.gettingyouraxeon.battleship.api.*
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class GameAggregate() {
    @AggregateIdentifier
    private var id: Long? = null
    private lateinit var game: Game

    @CommandHandler
    constructor(command: CreateGameCommand) : this() {
        val event = GameCreatedEvent(command.id, command.timestamp)
        applyEvent(event)
    }

    @EventSourcingHandler
    fun on(event: GameCreatedEvent) {
        id = event.id
        game = Game.created(event.id, event.timestamp)
    }

    @CommandHandler
    fun handle(command: JoinGameCommand) {
        val newGame = game.playerJoined(command.player, command.timestamp)
        applyEvent(PlayerJoinedEvent(newGame))
    }

    @EventSourcingHandler
    fun on(event: PlayerJoinedEvent) {
        game = event.newGame
    }

    @CommandHandler
    fun handle(command: PlaceShipCommand) {
        val newGame = game.shipPlaced(command.player, command.ship, command.timestamp)
        applyEvent(ShipPlacedEvent(newGame))
    }

    @EventSourcingHandler
    fun on(event: ShipPlacedEvent) {
        game = event.newGame
    }

    @CommandHandler
    fun handle(command: StartGameCommand) {
        val newGame = game.started(command.timestamp)
        applyEvent(GameStartedEvent(newGame))
    }

    @EventSourcingHandler
    fun on(event: GameStartedEvent) {
        game = event.newGame
    }

    @CommandHandler
    fun handle(command: ShootCommand) {
        val newGame = game.shot(command.player, command.x, command.y, command.timestamp)
        applyEvent(ShotEvent(newGame))

        val finishedGame = newGame.getFinishedGame(command.timestamp)
        if (finishedGame != null) {
            applyEvent(GameFinishedEvent(finishedGame))
        }
    }

    @EventSourcingHandler
    fun on(event: ShotEvent) {
        game = event.newGame
    }

    @EventSourcingHandler
    fun on(event: GameFinishedEvent) {
        game = event.newGame
    }
}
