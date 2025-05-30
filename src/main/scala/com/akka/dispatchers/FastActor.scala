// src/main/scala/com/example/dispatchers/FastActor.scala
package com.example.dispatchers

import akka.actor.{Actor, ActorLogging, Props}

object FastActor {
  def props(): Props = Props(new FastActor())
  case class FastTask(id: Int, payload: String)
  case class FastTaskResult(id: Int, result: String)
}

class FastActor extends Actor with ActorLogging {
  import FastActor._

  override def receive: Receive = {
    case FastTask(id, payload) =>
      val startTime = System.nanoTime()
      // Simulate quick, non-blocking work
      val result = s"Fast response for '$payload' (id: $id)"
      val duration = (System.nanoTime() - startTime) / 1_000_000.0
      // log.info(s"FastActor processed task $id in $duration ms on ${Thread.currentThread().getName}")
      sender() ! FastTaskResult(id, result)
  }
}