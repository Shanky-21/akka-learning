package com.akka.persistence

import com.akka.persistence.CounterActorEvents._

// State: the in-memory representation of the actor's data
case class CounterActorState(currentValue: Int = 0) {

  def updated(event: CounterEvent): CounterActorState = event match {
    case Incremented(value) => copy(currentValue = currentValue + value)
    case Decremented(value) => copy(currentValue = currentValue - value)
  }
}