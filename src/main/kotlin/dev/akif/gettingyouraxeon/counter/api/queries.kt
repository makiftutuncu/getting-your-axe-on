package dev.akif.gettingyouraxeon.counter.api

import java.util.UUID

data class ListCountersQuery(val page: Int, val size: Int)

data class GetCounterQuery(val id: UUID)

data class WatchCounterQuery(val id: UUID)
