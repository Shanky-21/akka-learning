package com.akka.persistence

// Commands: represent intent to change state
object CounterActorCommands {
  sealed trait CounterCommand
  case class Increment(value: Int) extends CounterCommand
  case class Decrement(value: Int) extends CounterCommand
  case object GetCurrentValue extends CounterCommand
  case object PrintState extends CounterCommand // For debugging
  case object TakeSnapshotPlease extends CounterCommand // To trigger snapshot
}