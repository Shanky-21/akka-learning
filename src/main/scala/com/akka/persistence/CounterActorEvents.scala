package com.akka.persistence

// Events: represent state changes that have occurred and are persisted
object CounterActorEvents {
  sealed trait CounterEvent
  case class Incremented(value: Int) extends CounterEvent
  case class Decremented(value: Int) extends CounterEvent
}