package dev.akif.gettingyouraxeon.counter.api

import java.time.Instant

enum class AdjustmentType {
    Increment,
    Decrement
}

data class CounterAdjustment(val type: AdjustmentType, val by: Int)

data class CounterResponse(
    val name: String,
    val value: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
