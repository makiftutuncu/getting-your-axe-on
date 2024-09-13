package dev.akif.gettingyouraxeon.battleship

import dev.akif.gettingyouraxeon.battleship.api.Coordinates
import dev.akif.gettingyouraxeon.battleship.api.GameResponse
import dev.akif.gettingyouraxeon.battleship.api.PlaceShipRequest
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/games")
class GameController(private val games: GameService) {
    @Operation(summary = "Create a new game")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): Mono<ResponseEntity<Void>> =
        games
            .create()
            .map { ResponseEntity.created(URI.create("/games/$it")).build() }

    @GetMapping("/{id}")
    @Operation(summary = "Get game with given id")
    fun get(@PathVariable id: Long): Mono<GameResponse> =
        games.get(id).map { it.toResponse() }

    @GetMapping
    @Operation(summary = "List existing games")
    fun list(): Flux<GameResponse> =
        games.list().map { it.toResponse() }

    @Operation(summary = "Join game with given id as given player")
    @PutMapping("/{id}/players/{player}")
    fun join(
        @PathVariable id: Long,
        @PathVariable player: Player
    ): Flux<ServerSentEvent<GameResponse>> =
        games.join(id, player).map {
            ServerSentEvent
                .builder(it.toResponse())
                .id(it.id.toString())
                .build()
        }

    @Operation(summary = "Place given ship as given player to game with given id")
    @PostMapping("/{id}/players/{player}/ships")
    fun placeShip(
        @PathVariable id: Long,
        @PathVariable player: Player,
        @RequestBody ship: PlaceShipRequest
    ): Mono<GameResponse> =
        games
            .placeShip(id, player, ship)
            .map { it.toResponse() }

    @Operation(summary = "Start game with given id")
    @PutMapping("/{id}")
    fun start(@PathVariable id: Long): Mono<GameResponse> =
        games.start(id).map { it.toResponse() }

    @Operation(summary = "Shoot to given coordinates as given player in game with given id")
    @PostMapping("/{id}/players/{player}/shots")
    fun shoot(
        @PathVariable id: Long,
        @PathVariable player: Player,
        @RequestBody coordinates: Coordinates
    ): Mono<GameResponse> =
        games
            .shoot(id, player, coordinates)
            .map { it.toResponse() }

    private fun Game.toResponse() = GameResponse(
        rendered = board.toString(),
        board = board.cells.map { it.map { c -> c.symbol } },
        ships = ships,
        status = status,
        turn = turn,
        winner = winner,
        createdAt = createdAt,
        updatedAt = updatedAt,
        startedAt = startedAt,
        finishedAt = finishedAt
    )
}
