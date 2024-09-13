package dev.akif.gettingyouraxeon.battleship.api

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class GameNotFoundException(id: Long): ResponseStatusException(
    HttpStatus.NOT_FOUND,
    "Game $id is not found"
)
