package dev.akif.gettingyouraxeon.battleship.api.model

data class Board(val cells: Array<Array<Cell>>) {
    companion object {
        const val SIZE: Int = 10
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
            when (direction) {
                Direction.Horizontal -> {
                    for (i in x until x + size) {
                        this[i, y] = newCell
                    }
                }

                Direction.Vertical -> {
                    for (j in y until y + size) {
                        this[x, j] = newCell
                    }
                }
            }
        }
    }

    fun shot(player: Player, x: Int, y: Int): Pair<Board, Cell> {
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

        val newBoard = Board(Array(SIZE) { j -> Array(SIZE) { i -> this[i, j] } }).apply {
            this[x, y] = newCell
        }

        return newBoard to newCell
    }

    fun asPlayer(player: Player): Board =
        copy(
            cells = cells.map { row ->
                row.map { cell ->
                    when (cell) {
                        Cell.ShipA -> if (player == Player.A) Cell.ShipA else Cell.Empty
                        Cell.ShipB -> if (player == Player.B) Cell.ShipB else Cell.Empty
                        else -> cell
                    }
                }.toTypedArray()
            }.toTypedArray()
        )

    override fun toString(): String {
        val firstLine = (0 until SIZE).joinToString(separator = " ", prefix = "  ") { "$it" }
        val lines = (0 until SIZE).joinToString(separator = "\n") { y ->
            (0 until SIZE).joinToString(separator = " ", prefix = "$y ") { x ->
                this[x, y].symbol.toString()
            }
        }
        return "$firstLine\n$lines"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Board) return false

        if (!cells.contentDeepEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentDeepHashCode()
    }
}
