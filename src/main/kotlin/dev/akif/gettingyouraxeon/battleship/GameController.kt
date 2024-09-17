package dev.akif.gettingyouraxeon.battleship

import dev.akif.gettingyouraxeon.battleship.api.Coordinates
import dev.akif.gettingyouraxeon.battleship.api.GameResponse
import dev.akif.gettingyouraxeon.battleship.api.PlaceShipRequest
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "Stream existing games")
    fun streamGames(): Flux<List<GameResponse>> =
        games.streamGames().map { games -> games.map { it.toResponse() } }

    @Operation(summary = "Join game with given id as given player")
    @PutMapping("/{id}/players/{player}")
    fun join(
        @PathVariable id: Long,
        @PathVariable player: Player
    ): Mono<Void> =
        games.join(id, player)

    @Operation(summary = "Stream game with given id")
    @GetMapping("/{id}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamGame(
        @PathVariable id: Long
    ): Flux<GameResponse> =
        games.streamGame(id).map { it.toResponse() }

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
        id = id,
        boards = boards.mapValues { it.value.cells.map { row -> row.map { c -> c.symbol } } },
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
