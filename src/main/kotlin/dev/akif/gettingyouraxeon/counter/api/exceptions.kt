package dev.akif.gettingyouraxeon.counter.api

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CounterNotFoundException(name: String): ResponseStatusException(
    HttpStatus.NOT_FOUND,
    "Counter '$name' is not found"
)
