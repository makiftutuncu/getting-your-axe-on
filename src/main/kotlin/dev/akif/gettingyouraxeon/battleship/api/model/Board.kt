package dev.akif.gettingyouraxeon.battleship.api.model

@JvmInline
value class Board(private val cells: Array<Array<Cell>>) {
    companion object {
        const val SIZE: Int = 8
    }

    constructor() : this(Array(SIZE) { Array(SIZE) { Cell.Empty } })

    operator fun get(x: Int, y: Int): Cell = cells[y][x]

    operator fun set(x: Int, y: Int, cell: Cell) {
        cells[y][x] = cell
    }

    fun shipPlaced(player: Player, ship: PlacedShip): Board {
        val (x, y, direction, type) = ship
        val size = type.size

        require(x in 0 until SIZE) { "Invalid x coordinate: $x" }
        require(y in 0 until SIZE) { "Invalid y coordinate: $y" }

        when (direction) {
            Direction.Horizontal -> {
                require(x + size < SIZE) {
                    "Cannot place ${type.name} horizontally at ($x, $y) because it does not fit"
                }
                require((x until (x + size)).all { this[it, y] == Cell.Empty }) {
                    "Cannot place ${type.name} horizontally at ($x, $y) because it is already occupied"
                }
            }

            Direction.Vertical -> {
                require(y + size < SIZE) {
                    "Cannot place ${type.name} vertically at ($x, $y) because it does not fit"
                }
                require((y until (y + size)).all { this[x, it] == Cell.Empty }) {
                    "Cannot place ${type.name} vertically at ($x, $y) because it is already occupied"
                }
            }
        }

        val newCell = when (player) {
            Player.A -> Cell.ShipA
            Player.B -> Cell.ShipB
        }

        return Board(Array(SIZE) { j -> Array(SIZE) { i -> this[i, j] } }).apply {
            this[x, y] = newCell
        }
    }

    fun shot(player: Player, x: Int, y: Int): Board {
        require(this[x, y] != Cell.Hit && this[x, y] != Cell.Miss) {
            "Player $player cannot shoot at ($x, $y) because it has already been shot"
        }

        require(
            when (player) {
                Player.A -> this[x, y] != Cell.ShipA
                Player.B -> this[x, y] != Cell.ShipB
            }
        ) {
            "Player $player cannot shoot at ($x, $y) because they have a ship there"
        }

        val newCell = when (this[x, y]) {
            Cell.ShipA, Cell.ShipB -> Cell.Hit
            else -> Cell.Miss
        }

        return Board(Array(SIZE) { j -> Array(SIZE) { i -> this[i, j] } }).apply {
            this[x, y] = newCell
        }
    }

    override fun toString(): String =
        (0 until SIZE).joinToString(separator = "\n") { y ->
            (0 until SIZE).joinToString(separator = " ") { x ->
                if (y == 0) {
                    "${if (x == 0) "  " else ""}${x + 1}"
                } else if (x == 0) {
                    "  ${'A' + y - 1}"
                } else {
                    this[x, y].symbol.toString()
                }
            }
        }
}
