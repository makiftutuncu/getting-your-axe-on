package dev.akif.gettingyouraxeon.battleship.api

import dev.akif.gettingyouraxeon.battleship.api.model.Player

data object GetNextIdQuery

data class GetGameQuery(
    val id: Long
)

data class StreamGameQuery(
    val id: Long,
    val player: Player
)

data object ListGamesQuery
