package dev.akif.battleships.api.model

import dev.akif.battleships.api.InvalidMoveException.Companion.validate
import java.time.Instant

data class Game(
    val id: Long,
    val boards: Map<Player, Board>,
    val ships: Map<Player, List<PlacedShip>>,
    val status: GameStatus,
    val turn: Player,
    val winner: Player?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val startedAt: Instant?,
    val finishedAt: Instant?
) {
    companion object {
        fun created(id: Long, now: Instant): Game =
            Game(
                id = id,
                boards = emptyMap(),
                ships = emptyMap(),
                status = GameStatus.Created,
                turn = Player.A,
                winner = null,
                createdAt = now,
                updatedAt = now,
                startedAt = null,
                finishedAt = null
            )
    }

    operator fun get(player: Player, x: Int, y: Int): Cell = boards.getValue(player)[x, y]

    fun playerJoined(player: Player, now: Instant): Game {
        validate(status == GameStatus.Created) { "Cannot join game $id because it is not in created state" }

        validate(player !in boards) { "Cannot join game $id because player $player has already joined" }

        return copy(
            boards = boards + (player to Board()),
            ships = ships + (player to emptyList()),
            updatedAt = now
        )
    }

    fun shipPlaced(player: Player, ship: PlacedShip, now: Instant): Game {
        validate(status == GameStatus.Created) {
            "Cannot place ship because game $id is not in created state"
        }

        validate(ships[player].orEmpty().none { it.type == ship.type }) {
            "Cannot place ${ship.type} for player $player because it is already placed"
        }

        val newBoards = boards[player]?.let { boards + (player to it.shipPlaced(player, ship)) } ?: boards
        val newShips = ships + (player to (ships[player].orEmpty() + ship))

        return copy(
            boards = newBoards,
            ships = newShips,
            updatedAt = now
        )
    }

    fun started(now: Instant): Game {
        validate(status == GameStatus.Created) {
            "Cannot start game $id because it is not in created state"
        }

        Player.entries.forEach { p ->
            validate(ships[p].orEmpty().map { it.type }.toSet() == ShipType.entries.toSet()) {
                "Cannot start game because not Player $p has not placed all of their ships"
            }
        }

        return copy(
            status = GameStatus.Started,
            turn = Player.A,
            updatedAt = now,
            startedAt = now
        )
    }

    fun shot(player: Player, x: Int, y: Int, now: Instant): Pair<Game, Player?> {
        validate(status == GameStatus.Started) { "Cannot shoot because game $id is not started" }

        validate(player == turn) { "Player $player cannot shoot because it is not their turn" }

        val otherPlayer = player.other

        val (newBoards, newTurn) = boards[otherPlayer]?.let {
            val (newBoard, newCell) = it.shot(player, x, y)
            val newBoards = boards + (otherPlayer to newBoard)
            val newTurn = if (newCell == Cell.Miss) turn.other else turn
            newBoards to newTurn
        } ?: (boards to turn)

        val newGame = copy(
            boards = newBoards,
            turn = newTurn,
            updatedAt = now
        )

        val winner = Player
            .entries
            .firstOrNull { p -> newBoards[p]?.let { board -> ships[p]?.all { it.isSunken(board) } } == true }
            ?.other

        return newGame to winner
    }

    fun finished(winner: Player, now: Instant): Game =
        copy(
            status = GameStatus.Finished,
            winner = winner,
            updatedAt = now,
            finishedAt = now
        )

    override fun toString(): String =
        """
        |
        |A=${boards[Player.A]}
        |B=${boards[Player.B]}
        |id=$id, status=$status, turn=$turn, winner=$winner,
        |c=$createdAt, u=$updatedAt, s=$startedAt, f=$finishedAt
        |""".trimMargin()
}
