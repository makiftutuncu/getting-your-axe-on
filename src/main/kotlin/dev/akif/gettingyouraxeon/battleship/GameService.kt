package dev.akif.gettingyouraxeon.battleship

import dev.akif.gettingyouraxeon.battleship.api.*
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import dev.akif.gettingyouraxeon.battleship.api.model.PlacedShip
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class GameService(
    private val commands: ReactorCommandGateway,
    private val queries: ReactorQueryGateway
) {
    fun create(): Mono<Long> =
        queries
            .query(GetNextIdQuery, Long::class.java)
            .flatMap { commands.send<Any>(CreateGameCommand(it)).then(Mono.just(it)) }

    fun get(id: Long): Mono<Game> =
        queries
            .query(GetGameQuery(id), Game::class.java)
            .switchIfEmpty(Mono.error(GameNotFoundException(id)))

    fun placeShip(id: Long, player: Player, ship: PlaceShipRequest): Mono<Game> =
        get(id).flatMap { game ->
            val placedShip = ship.toPlacedShip()
            commands
                .send<Any>(PlaceShipCommand(id, player, placedShip))
                .then(Mono.just(game.shipPlaced(player, placedShip, Instant.now())))

        }

    private fun PlaceShipRequest.toPlacedShip() = PlacedShip(x, y, direction, type)
}
