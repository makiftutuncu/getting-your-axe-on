package dev.akif.gettingyouraxeon.counter.command

import dev.akif.gettingyouraxeon.counter.api.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
class CounterAggregate() {
    @AggregateIdentifier
    private lateinit var id: UUID
    private var value: Int = 0

    @CommandHandler
    constructor(command: CreateCounterCommand): this() {
        applyEvent(CounterCreatedEvent(UUID.randomUUID(), command.name))
    }

    @EventSourcingHandler
    fun on(event: CounterCreatedEvent) {
        id = event.id
    }

    @CommandHandler
    fun handle(command: ChangeCounterCommand) {
        applyEvent(
            CounterChangedEvent(id = id, oldValue = value, newValue = value + command.adjustment)
        )
    }

    @EventSourcingHandler
    fun on(event: CounterChangedEvent) {
        value = event.newValue
    }

    @CommandHandler
    fun handle(command: DeleteCounterCommand) {
        applyEvent(CounterDeletedEvent(id))
    }

    @EventSourcingHandler
    fun on(event: CounterDeletedEvent) {
        markDeleted()
    }
}
