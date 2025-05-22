package com.example

import akka.actor.{Actor, Props, ActorLogging}

object Counter {

  def props: Props = Props[Counter]()

  case object Increment
  case object Decrement
  case object GetCount
}


class Counter extends Actor with ActorLogging {

  import Counter._

  // internal mutable state

  private var currentCount = 0

  override def receive: Receive = {
    case Increment => 
      currentCount += 1
      log.info(s"Incremented: $currentCount")
    case Decrement => 
      currentCount -= 1
      log.info(s"Decremented: $currentCount")
    case GetCount => 
      sender() ! currentCount
      log.info(s"GetCount: $currentCount")
  }


}


