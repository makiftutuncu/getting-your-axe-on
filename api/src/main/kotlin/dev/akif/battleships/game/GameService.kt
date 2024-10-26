package dev.akif.battleships.game

import com.fasterxml.jackson.databind.ObjectMapper
import dev.akif.battleships.api.*
import dev.akif.battleships.api.model.Game
import dev.akif.battleships.api.model.PlacedShip
import dev.akif.battleships.api.model.Player
import dev.akif.battleships.query.GameEntity
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GameService(
    private val commands: ReactorCommandGateway,
    private val queries: ReactorQueryGateway,
    private val games: GameRepository,
    private val mapper: ObjectMapper
) {
    @Transactional
    fun create(): Mono<Game> {
        val game = games.save(GameEntity())
        return commands
            .send<Any>(CreateGameCommand(game.id))
            .then(Mono.just(game.toGame(mapper)))
    }

    fun get(id: Long): Mono<Game> =
        queries
            .query(GetGameQuery(id), ResponseTypes.instanceOf(Game::class.java))
            .switchIfEmpty(Mono.error(GameNotFoundException(id)))

    fun streamGames(): Flux<List<Game>> =
        queries
            .subscriptionQuery(StreamGamesQuery, ResponseTypes.multipleInstancesOf(Game::class.java))

    fun join(id: Long, player: Player): Mono<Void> =
        commands
            .send<Any>(JoinGameCommand(id, player))
            .then()

    fun streamGame(id: Long): Flux<Game> =
        queries.subscriptionQuery(
            GetGameQuery(id),
            ResponseTypes.instanceOf(Game::class.java)
        )

    fun placeShip(id: Long, player: Player, ship: PlaceShipRequest): Mono<Game> =
        commands
            .send<Any>(PlaceShipCommand(id, player, ship.toPlacedShip()))
            .then(get(id))

    fun start(id: Long): Mono<Game> =
        commands
            .send<Any>(StartGameCommand(id))
            .then(get(id))

    fun shoot(id: Long, player: Player, coordinates: Coordinates): Mono<Game> =
        commands
            .send<Any>(ShootCommand(id, player, coordinates.x, coordinates.y))
            .then(get(id))

    private fun PlaceShipRequest.toPlacedShip(): PlacedShip =
        PlacedShip(x, y, direction, type)
}
