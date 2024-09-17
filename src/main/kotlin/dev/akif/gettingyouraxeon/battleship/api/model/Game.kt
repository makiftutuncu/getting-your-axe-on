package dev.akif.gettingyouraxeon.battleship.api.model

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
        require(status == GameStatus.Created) { "Cannot join game $id because it is not in created state" }

        require(player !in boards) { "Cannot join game $id because player $player has already joined" }

        return copy(
            boards = boards + (player to Board()),
            ships = ships + (player to emptyList()),
            updatedAt = now
        )
    }

    fun shipPlaced(player: Player, ship: PlacedShip, now: Instant): Game {
        require(status == GameStatus.Created) {
            "Cannot place ship because game $id is not in created state"
        }

        require(ships[player].orEmpty().none { it.type == ship.type }) {
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
        require(status == GameStatus.Created) {
            "Cannot start game $id because it is not in created state"
        }

        Player.entries.forEach { p ->
            require(ships[p].orEmpty().map { it.type }.toSet() == ShipType.entries.toSet()) {
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

    fun shot(player: Player, x: Int, y: Int, now: Instant): Game {
        require(status == GameStatus.Started) { "Cannot shoot because game $id is not started" }

        require(player == turn) { "Player $player cannot shoot because it is not their turn" }

        val otherPlayer = if (player == Player.A) Player.B else Player.A

        val (newBoards, newTurn) = boards[otherPlayer]?.let {
            val (newBoard, newCell) = it.shot(player, x, y)
            val newBoards = boards + (otherPlayer to newBoard)
            val newTurn = if (newCell == Cell.Miss) {
                if (turn == Player.A) Player.B else Player.A
            } else {
                turn
            }
            newBoards to newTurn
        } ?: (boards to turn)

        return copy(
            boards = newBoards,
            turn = newTurn,
            updatedAt = now
        )
    }

    fun getFinishedGame(now: Instant): Game? =
        Player
            .entries
            .firstOrNull { player ->
                boards[player]?.let { board -> ships[player]?.all { it.isSunken(board) } } == true
            }
            ?.other
            ?.let { winner ->
                copy(
                    status = GameStatus.Finished,
                    winner = winner,
                    updatedAt = now,
                    finishedAt = now
                )
            }

    override fun toString(): String =
        """
        |
        |A=${boards[Player.A]}
        |B=${boards[Player.B]}
        |id=$id, status=$status, turn=$turn, winner=$winner,
        |c=$createdAt, u=$updatedAt, s=$startedAt, f=$finishedAt
        |""".trimMargin()
}
