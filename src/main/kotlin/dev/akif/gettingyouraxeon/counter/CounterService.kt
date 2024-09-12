package dev.akif.gettingyouraxeon.counter

import dev.akif.gettingyouraxeon.counter.api.*
import dev.akif.gettingyouraxeon.counter.query.Counter
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CounterService(
    private val commands: ReactorCommandGateway,
    private val queries: ReactorQueryGateway
) {
    fun list(page: Int, size: Int): Flux<Counter> =
        queries
            .query(ListCountersQuery(page, size), ResponseTypes.multipleInstancesOf(Counter::class.java))
            .flatMapMany { Flux.fromIterable(it) }

    fun create(name: String): Mono<Void> =
        commands.send<Any>(CreateCounterCommand(name)).then()

    fun get(name: String): Mono<Counter> =
        queries
            .query(GetCounterQuery(name), Counter::class.java)
            .switchIfEmpty(Mono.error(CounterNotFoundException(name)))

    fun delete(name: String): Mono<Void> =
        commands.send(DeleteCounterCommand(name))

    fun change(name: String, adjustment: CounterAdjustment): Mono<Counter> =
        commands.send(ChangeCounterCommand(name, adjustment))

    fun watch(name: String): Flux<Counter> =
        queries.subscriptionQuery(WatchCounterQuery(name), Counter::class.java)
}
