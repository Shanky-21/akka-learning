// src/main/scala/com/example/dispatchers/SlowBlockingActor.scala
package com.akka.dispatchers

import akka.actor.{Actor, ActorLogging, Props}

object SlowBlockingActor {
  def props(): Props = Props(new SlowBlockingActor())
  case class SlowTask(id: Int, durationMs: Long)
  case class SlowTaskResult(id: Int, message: String)
}

class SlowBlockingActor extends Actor with ActorLogging {
  import SlowBlockingActor._

  override def receive: Receive = {
    case SlowTask(id, durationMs) =>
      val startTime = System.currentTimeMillis()
      log.info(s"SlowBlockingActor starting task $id (block for $durationMs ms) on ${Thread.currentThread().getName}...")
      Thread.sleep(durationMs) // <<-- THE BLOCKING CALL!
      val actualDuration = System.currentTimeMillis() - startTime
      log.info(s"SlowBlockingActor finished task $id. Actual duration: $actualDuration ms.")
      sender() ! SlowTaskResult(id, s"Slow task $id completed after $actualDuration ms")
  }
}