package dev.akif.gettingyouraxeon.counter.query

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CounterRepository: R2dbcRepository<Counter, UUID>
