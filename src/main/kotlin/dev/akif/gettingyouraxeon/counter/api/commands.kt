package dev.akif.gettingyouraxeon.counter.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

sealed interface CounterCommand

data class CreateCounterCommand(val name: String): CounterCommand

data class ChangeCounterCommand(@TargetAggregateIdentifier val id: UUID, val name: String?, val adjustment: Int): CounterCommand

data class DeleteCounterCommand(@TargetAggregateIdentifier val id: UUID): CounterCommand
