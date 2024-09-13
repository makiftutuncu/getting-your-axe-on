package dev.akif.gettingyouraxeon.battleship.query

import dev.akif.gettingyouraxeon.battleship.api.*
import dev.akif.gettingyouraxeon.battleship.api.model.Game
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameProjection(private val updates: QueryUpdateEmitter) {
    private val games = TreeMap<Long, Game>(Long::compareTo)

    @QueryHandler
    fun handle(query: GetNextIdQuery): Long =
        if (games.isEmpty()) 1L else games.lastKey() + 1

    @QueryHandler
    fun handle(query: GetGameQuery): Game? =
        games[query.id]

    @EventHandler
    fun on(event: GameCreatedEvent) {
        val game = Game.created(event.id, event.timestamp)
        games[event.id] = game
        updates.emit<GetGameQuery, Game>(GetGameQuery::class.java, { it.id == event.id }, game)
    }

    @EventHandler
    fun on(event: ShipPlacedEvent) {
        event.update { it.newGame }
    }

    @EventHandler
    fun on(event: GameStartedEvent) {
        event.update { it.newGame }
    }

    @EventHandler
    fun on(event: ShotEvent) {
        event.update { it.newGame }
    }

    @EventHandler
    fun on(event: GameFinishedEvent) {
        event.update { it.newGame }
    }

    private fun <E> E.update(getGame: (E) -> Game) {
        val newGame = getGame(this)
        val id = newGame.id
        if (id !in games) {
            throw GameNotFoundException(id)
        }
        games[id] = newGame
        updates.emit<GetGameQuery, Game>(GetGameQuery::class.java, { it.id == id }, newGame)
    }
}
