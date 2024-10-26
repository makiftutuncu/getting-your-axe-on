package dev.akif.battleships.query

import com.fasterxml.jackson.databind.ObjectMapper
import dev.akif.battleships.api.*
import dev.akif.battleships.api.model.Game
import dev.akif.battleships.game.GameRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

@Service
class GameProjection(
    private val updates: QueryUpdateEmitter,
    private val games: GameRepository,
    private val mapper: ObjectMapper
) {
    @QueryHandler
    fun handle(query: GetGameQuery): Game? =
        games.findById(query.id).getOrNull()?.toGame(mapper)

    @QueryHandler
    fun handle(query: StreamGamesQuery): List<Game> =
        listGames()

    @EventHandler
    @Transactional
    fun on(event: GameCreatedEvent, @Timestamp now: Instant) {
        val game = Game.created(event.id, now)
        games.save(GameEntity.fromGame(mapper, game))
        updates.emit<GetGameQuery, Game>(GetGameQuery::class.java, { it.id == event.id }, game)
        updates.emit<StreamGamesQuery, List<Game>>(StreamGamesQuery::class.java, { true }, listGames())
    }

    @EventHandler
    fun on(event: PlayerJoinedEvent, @Timestamp now: Instant) {
        event.applyToGameAs { playerJoined(event.player, now) }
    }

    @EventHandler
    fun on(event: ShipPlacedEvent, @Timestamp now: Instant) {
        event.applyToGameAs { shipPlaced(event.player, event.ship, now) }
    }

    @EventHandler
    fun on(event: GameStartedEvent, @Timestamp now: Instant) {
        event.applyToGameAs { started(now) }
    }

    @EventHandler
    fun on(event: ShotEvent, @Timestamp now: Instant) {
        event.applyToGameAs {
            val (newGame, _) = shot(event.player, event.x, event.y, now)
            newGame
        }
    }

    @EventHandler
    fun on(event: GameFinishedEvent, @Timestamp now: Instant) {
        event.applyToGameAs { finished(event.winner, now) }
    }

    @Transactional
    fun <E: GameEvent> E.applyToGameAs(action: Game.() -> Game) {
        val game = games.findById(this.id).orElseThrow { GameNotFoundException(id) }
        val newGame = action(game.toGame(mapper))
        games.save(GameEntity.fromGame(mapper, newGame))
        updates.emit<GetGameQuery, Game>(GetGameQuery::class.java, { it.id == id }, newGame)
        updates.emit<StreamGamesQuery, List<Game>>(StreamGamesQuery::class.java, { true }, listGames())
    }

    private fun listGames(): List<Game> =
        games.findAll(Sort.by(Sort.Order.desc("createdAt"))).map { it.toGame(mapper) }
}
