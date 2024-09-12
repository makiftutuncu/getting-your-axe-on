package dev.akif.gettingyouraxeon.counter.command

import dev.akif.gettingyouraxeon.counter.api.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class CounterAggregate() {
    @AggregateIdentifier
    private var name: String = ""
    private var value: Int = 0

    @CommandHandler
    constructor(command: CreateCounterCommand): this() {
        applyEvent(CounterCreatedEvent(command.name))
    }

    @EventSourcingHandler
    fun on(event: CounterCreatedEvent) {
        name = event.name
    }

    @CommandHandler
    fun handle(command: ChangeCounterCommand) {
        val newValue = command.adjustment.run {
            when (type) {
                AdjustmentType.Increment -> value + by
                AdjustmentType.Decrement -> value - by
            }
        }
        applyEvent(CounterChangedEvent(name, value, newValue))
    }

    @EventSourcingHandler
    fun on(event: CounterChangedEvent) {
        value = event.newValue
    }

    @CommandHandler
    fun handle(command: DeleteCounterCommand) {
        applyEvent(CounterDeletedEvent(command.name))
    }

    @EventSourcingHandler
    fun on(event: CounterDeletedEvent) {
        markDeleted()
    }
}
