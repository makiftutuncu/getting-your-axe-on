package dev.akif.gettingyouraxeon.battleship.api.model

import java.time.Instant

data class Game(
    val id: Long,
    val board: Board,
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
                board = Board(),
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

    fun playerJoined(player: Player, now: Instant): Game {
        require(status == GameStatus.Created) { "Cannot join game $id because it is not in created state" }

        require(player !in ships) { "Cannot join game $id because player $player has already joined" }

        return copy(
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

        val newBoard = board.shipPlaced(player, ship)
        val newShips = ships + (player to (ships[player].orEmpty() + ship))

        return copy(
            board = newBoard,
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

        val (newBoard, newCell) = board.shot(player, x, y)

        return copy(
            board = newBoard,
            turn = if (newCell == Cell.Miss) {
                if (turn == Player.A) Player.B else Player.A
            } else {
                turn
            },
            updatedAt = now
        )
    }

    fun getFinishedGame(now: Instant): Game? {
        val winner = when {
            ships[Player.A]?.all { it.isSunken(board) } == true -> Player.B
            ships[Player.B]?.all { it.isSunken(board) } == true -> Player.A
            else -> return null
        }

        return copy(
            status = GameStatus.Finished,
            winner = winner,
            updatedAt = now,
            finishedAt = now
        )
    }

    fun asPlayer(player: Player): Game =
        copy(
            board = board.asPlayer(player),
            ships = ships.filterKeys { it != player }
        )

    override fun toString(): String =
        """
        |
        |$board
        |id=$id, status=$status, turn=$turn, winner=$winner,
        |c=$createdAt, u=$updatedAt, s=$startedAt, f=$finishedAt
        |""".trimMargin()
}
