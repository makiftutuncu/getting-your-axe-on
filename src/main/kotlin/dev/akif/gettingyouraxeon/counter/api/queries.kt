package dev.akif.gettingyouraxeon.counter.api

data class ListCountersQuery(val page: Int, val size: Int)

data class GetCounterQuery(val name: String)

data class WatchCounterQuery(val name: String)
