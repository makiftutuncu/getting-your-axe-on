package dev.akif.gettingyouraxeon.counter.query

import dev.akif.gettingyouraxeon.counter.api.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

@Service
class CounterProjection(
    private val counters: CounterRepository,
    private val updates: QueryUpdateEmitter
) {
    @EventHandler
    fun on(event: CounterCreatedEvent) {
        val now = Instant.now()
        val counter = counters.save(Counter(event.name, 0, now, now))
        updates.emit<Counter>({ true }, counter)
    }

    @EventHandler
    fun on(event: CounterChangedEvent) {
        val counter = counters.findById(event.name).orElseThrow { CounterNotFoundException(event.name) }
        counter.value = event.newValue
        counter.updatedAt = Instant.now()
        val updated = counters.save(counter)
        updates.emit<Counter>({ true }, updated)
    }

    @EventHandler
    fun on(event: CounterDeletedEvent) {
        counters.deleteById(event.name)
        updates.emit<Counter>({ true }, null)
    }

    @QueryHandler
    fun handle(query: ListCountersQuery): List<Counter> =
        counters.findAll(PageRequest.of(query.page, query.size)).content

    @QueryHandler
    fun handle(query: GetCounterQuery): Counter? =
        counters.findById(query.name).getOrNull()

    @QueryHandler
    fun handle(query: WatchCounterQuery): Counter? =
        counters.findById(query.name).getOrNull()
}
