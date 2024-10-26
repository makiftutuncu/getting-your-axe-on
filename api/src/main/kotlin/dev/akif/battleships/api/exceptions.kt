package dev.akif.battleships.api

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class GameNotFoundException(id: Long): ResponseStatusException(
    HttpStatus.NOT_FOUND,
    "Game $id is not found"
)

// TODO: Replace uses of `require` with this
class InvalidMoveException(message: String): ResponseStatusException(
    HttpStatus.BAD_REQUEST,
    message
) {
    companion object {
        fun validate(requirement: Boolean, message: () -> String) {
            if (!requirement) {
                throw InvalidMoveException(message())
            }
        }
    }
}
