package com.example.routing

import akka.actor.{Actor, ActorLogging, Props}
import scala.util.Random

object WorkerActor {
  def props: Props = Props[WorkerActor]()
  
  // Messages
  case class Work(id: Int, data: String)
  case class WorkComplete(id: Int, result: String, workerId: Int)
}

class WorkerActor extends Actor with ActorLogging {
  import WorkerActor._
  
  // Each worker has a unique ID to identify which worker processed what
  private val workerId = Random.nextInt(10000)
  
  override def preStart(): Unit = {
    log.info(s"Worker $workerId started")
  }
  
  override def receive: Receive = {
    case Work(id, data) =>
      log.info(s"Worker $workerId processing work $id: $data")
      
      // Simulate processing time
      Thread.sleep(500 + Random.nextInt(1000))
      
      // Process the data (just uppercase it in this example)
      val result = data.toUpperCase
      
      // Send back the result
      sender() ! WorkComplete(id, result, workerId)
  }
}