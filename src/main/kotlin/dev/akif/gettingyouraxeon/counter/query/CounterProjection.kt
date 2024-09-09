package dev.akif.gettingyouraxeon.counter.query

import dev.akif.gettingyouraxeon.counter.api.CounterCreatedEvent
import dev.akif.gettingyouraxeon.counter.api.GetCounterQuery
import dev.akif.gettingyouraxeon.counter.api.ListCountersQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class CounterProjection(
    private val counters: CounterRepository
) {
    @EventHandler
    suspend fun on(event: CounterCreatedEvent) {
        val now = Instant.now()
        val counter = Counter(UUID.randomUUID(), event.name, 0, now, now)

        withContext(Dispatchers.IO) {
            counters
                .save(counter)
                .awaitSingle()
        }
    }

    @QueryHandler
    suspend fun handle(query: ListCountersQuery): Page<Counter> =
        withContext(Dispatchers.IO) {
            PageImpl(
                counters
                    .findAll()
                    .buffer(query.size, query.page * query.size)
                    .awaitFirstOrDefault(emptyList()),
                PageRequest.of(query.page, query.size),
                counters.count().awaitFirstOrElse { 0L }
            )
        }

    @QueryHandler
    suspend fun handle(query: GetCounterQuery): Counter? =
        withContext(Dispatchers.IO) {
            counters
                .findById(query.id)
                .awaitSingleOrNull()
        }
}
