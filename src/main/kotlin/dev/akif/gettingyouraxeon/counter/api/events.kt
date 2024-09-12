package dev.akif.gettingyouraxeon.counter.api

sealed interface CounterEvent

data class CounterCreatedEvent(val name: String): CounterEvent

data class CounterChangedEvent(val name: String, val oldValue: Int, val newValue: Int): CounterEvent

data class CounterDeletedEvent(val name: String): CounterEvent
