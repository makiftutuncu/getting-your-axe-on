package dev.akif.gettingyouraxeon.counter.query

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant
import java.util.UUID

@Entity
class Counter(
    @Id
    var id: UUID,

    @Column(unique = true)
    var name: String,

    var value: Int,

    var createdAt: Instant,

    var updatedAt: Instant
) {
    constructor(): this(
        id = UUID.randomUUID(),
        name = "",
        value = 0,
        createdAt = Instant.MIN,
        updatedAt = Instant.MIN
    )
}
