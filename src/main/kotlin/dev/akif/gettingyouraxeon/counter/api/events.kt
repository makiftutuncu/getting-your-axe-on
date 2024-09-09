package dev.akif.gettingyouraxeon.counter.api

import java.util.UUID

sealed interface CounterEvent

data class CounterCreatedEvent(val id: UUID, val name: String): CounterEvent

data class CounterChangedEvent(val id: UUID, val oldValue: Int, val newValue: Int): CounterEvent

data class CounterDeletedEvent(val id: UUID): CounterEvent
