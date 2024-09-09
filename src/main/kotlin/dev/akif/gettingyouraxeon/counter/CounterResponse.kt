package dev.akif.gettingyouraxeon.counter

import java.time.Instant

data class CounterResponse(val value: Int, val createdAt: Instant, val updatedAt: Instant)
