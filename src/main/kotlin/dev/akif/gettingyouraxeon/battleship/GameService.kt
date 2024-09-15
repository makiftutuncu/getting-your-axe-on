package dev.akif.gettingyouraxeon.battleship

import dev.akif.gettingyouraxeon.battleship.api.*
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import dev.akif.gettingyouraxeon.battleship.api.model.PlacedShip
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class GameService(
    private val commands: ReactorCommandGateway,
    private val queries: ReactorQueryGateway
) {
    private val now: Instant
        get() = Instant.now()

    fun create(): Mono<Long> =
        queries
            .query(GetNextIdQuery, ResponseTypes.instanceOf(Long::class.java))
            .flatMap { id ->
                commands
                    .send<Any>(CreateGameCommand(id, now))
                    .then(Mono.just(id))
            }

    fun get(id: Long): Mono<Game> =
        queries
            .query(GetGameQuery(id), ResponseTypes.instanceOf(Game::class.java))
            .switchIfEmpty(Mono.error(GameNotFoundException(id)))

    fun list(): Flux<Game> =
        queries
            .query(ListGamesQuery, ResponseTypes.multipleInstancesOf(Game::class.java))
            .flatMapMany { Flux.fromIterable(it) }

    fun join(id: Long, player: Player): Mono<Void> =
        commands
            .send<Any>(JoinGameCommand(id, player, now))
            .then()

    fun stream(id: Long, player: Player): Flux<Game> =
        queries.subscriptionQuery(
            GetGameQuery(id),
            ResponseTypes.instanceOf(Game::class.java)
        )

    fun placeShip(id: Long, player: Player, ship: PlaceShipRequest): Mono<Game> =
        get(id).flatMap { game ->
            val placedShip = ship.toPlacedShip()
            commands
                .send<Any>(PlaceShipCommand(id, player, placedShip, now))
                .then(Mono.just(game.shipPlaced(player, placedShip, now)))

        }

    fun start(id: Long): Mono<Game> =
        get(id).flatMap { game ->
            commands
                .send<Any>(StartGameCommand(id, now))
                .then(Mono.just(game.started(now)))
        }

    fun shoot(id: Long, player: Player, coordinates: Coordinates): Mono<Game> =
        get(id).flatMap { game ->
            commands
                .send<Any>(ShootCommand(id, player, coordinates.x, coordinates.y, now))
                .then(Mono.just(game.shot(player, coordinates.x, coordinates.y, now)))
        }

    private fun PlaceShipRequest.toPlacedShip() = PlacedShip(x, y, direction, type)
}
