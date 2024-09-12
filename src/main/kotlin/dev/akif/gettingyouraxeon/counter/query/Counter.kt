package dev.akif.gettingyouraxeon.counter.query

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant

@Entity
class Counter(
    @Id
    var name: String,

    var value: Int,

    var createdAt: Instant,

    var updatedAt: Instant
) {
    constructor(): this(
        name = "",
        value = 0,
        createdAt = Instant.MIN,
        updatedAt = Instant.MIN
    )
}
