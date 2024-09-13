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
    fun create(): Mono<Long> =
        queries
            .query(GetNextIdQuery, ResponseTypes.instanceOf(Long::class.java))
            .flatMap { id ->
                commands
                    .send<Any>(CreateGameCommand(id))
                    .then(Mono.just(id))
            }

    fun get(id: Long): Mono<Game> =
        queries
            .query(GetGameQuery(id), ResponseTypes.instanceOf(Game::class.java))
            .switchIfEmpty(Mono.error(GameNotFoundException(id)))

    fun join(id: Long, player: Player): Flux<Game> =
        commands
            .send<Any>(JoinGameCommand(id, player))
            .thenMany {
                queries.subscriptionQuery(
                    GetGameQuery(id),
                    ResponseTypes.instanceOf(Game::class.java)
                )
            }

    fun placeShip(id: Long, player: Player, ship: PlaceShipRequest): Mono<Game> =
        get(id).flatMap { game ->
            val placedShip = ship.toPlacedShip()
            commands
                .send<Any>(PlaceShipCommand(id, player, placedShip))
                .then(Mono.just(game.shipPlaced(player, placedShip, Instant.now())))

        }

    fun start(id: Long): Mono<Game> =
        get(id).flatMap { game ->
            commands
                .send<Any>(StartGameCommand(id))
                .then(Mono.just(game.started(Instant.now())))
        }

    fun shoot(id: Long, player: Player, coordinates: Coordinates): Mono<Game> =
        get(id).flatMap { game ->
            commands
                .send<Any>(ShootCommand(id, player, coordinates.x, coordinates.y))
                .then(Mono.just(game.shot(player, coordinates.x, coordinates.y, Instant.now())))
        }

    private fun PlaceShipRequest.toPlacedShip() = PlacedShip(x, y, direction, type, type.size)
}
