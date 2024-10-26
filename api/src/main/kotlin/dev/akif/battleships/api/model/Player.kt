package dev.akif.battleships.api.model

enum class Player {
    A,
    B;

    val other: Player
        get() = when (this) {
            A -> B
            B -> A
        }
}
