package dev.akif.gettingyouraxeon.battleship

import dev.akif.gettingyouraxeon.battleship.api.GameResponse
import dev.akif.gettingyouraxeon.battleship.api.PlaceShipRequest
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import dev.akif.gettingyouraxeon.battleship.api.model.Player
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/games")
class GameController(private val games: GameService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): Mono<ResponseEntity<Void>> =
        games
            .create()
            .map { ResponseEntity.created(URI.create("/games/$it")).build() }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Mono<GameResponse> =
        games.get(id).map { it.toResponse() }

    @PostMapping("/{id}/players/{player}/ships")
    fun placeShip(
        @PathVariable id: Long,
        @PathVariable player: Player,
        @RequestBody ship: PlaceShipRequest
    ): Mono<GameResponse> =
        games
            .placeShip(id, player, ship)
            .map { it.toResponse() }

    private fun Game.toResponse() = GameResponse(
        rendered = board.toString(),
        board = board,
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
