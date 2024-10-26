package dev.akif.battleships.query

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.akif.battleships.api.model.*
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.Instant

@Entity
@Suppress("LongParameterList")
@Table(name = "games")
class GameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "boards", nullable = false)
    val boards: String = "{}",

    @Column(name = "ships", nullable = false)
    val ships: String = "{}",

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: GameStatus = GameStatus.Created,

    @Column(name = "turn", nullable = false)
    @Enumerated(EnumType.STRING)
    val turn: Player = Player.A,

    @Column(name = "winner")
    @Enumerated(EnumType.STRING)
    val winner: Player? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),

    @Column(name = "started_at")
    val startedAt: Instant? = null,

    @Column(name = "finished_at")
    val finishedAt: Instant? = null
) {
    companion object {
        fun fromGame(mapper: ObjectMapper, game: Game): GameEntity =
            GameEntity(
                id = game.id,
                boards = mapper.writeValueAsString(game.boards),
                ships = mapper.writeValueAsString(game.ships),
                status = game.status,
                turn = game.turn,
                winner = game.winner,
                createdAt = game.createdAt,
                updatedAt = game.updatedAt,
                startedAt = game.startedAt,
                finishedAt = game.finishedAt
            )

        private val boardsReference: TypeReference<Map<Player, Board>> =
            object : TypeReference<Map<Player, Board>>() {}

        private val shipsReference: TypeReference<Map<Player, List<PlacedShip>>> =
            object : TypeReference<Map<Player, List<PlacedShip>>>() {}
    }

    fun toGame(mapper: ObjectMapper): Game =
        Game(
            id = id,
            boards = mapper.readValue(this.boards, boardsReference),
            ships = mapper.readValue(this.ships, shipsReference),
            status = status,
            turn = turn,
            winner = winner,
            createdAt = createdAt,
            updatedAt = updatedAt,
            startedAt = startedAt,
            finishedAt = finishedAt
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (effectiveClass(this) != effectiveClass(other)) return false
        return this.id == (other as GameEntity).id
    }

    override fun hashCode(): Int = effectiveClass(this).hashCode()

    private fun effectiveClass(thing: Any): Class<*> =
        if (thing is HibernateProxy) {
            thing.hibernateLazyInitializer.persistentClass
        } else {
            thing.javaClass
        }
}
