package dev.akif.gettingyouraxeon.counter.api

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

class CounterNotFoundException(id: UUID): ResponseStatusException(HttpStatus.NOT_FOUND, "Counter with id $id not found")
