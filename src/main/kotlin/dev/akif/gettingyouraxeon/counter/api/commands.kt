package dev.akif.gettingyouraxeon.counter.api

import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed interface CounterCommand

data class CreateCounterCommand(
    @TargetAggregateIdentifier val name: String
): CounterCommand

data class ChangeCounterCommand(
    @TargetAggregateIdentifier val name: String,
    val adjustment: CounterAdjustment
): CounterCommand

data class DeleteCounterCommand(
    @TargetAggregateIdentifier val name: String
): CounterCommand
