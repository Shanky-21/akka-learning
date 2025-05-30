package com.akka.routing

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.akka.routing.MasterActor.{BatchComplete, ProcessBatch}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object RouterDemo extends App {
  val system = ActorSystem("RouterDemo")
  implicit val timeout: Timeout = Timeout(10.seconds)
  
  // Create a master with 5 workers
  val master = system.actorOf(MasterActor.props(5), "master")
  
  // Create a batch of work
  val batch = (1 to 10).map(i => s"task-$i")
  
  println("Sending batch of work to master...")
  
  // Send work and wait for results
  (master ? ProcessBatch(batch)).mapTo[BatchComplete].onComplete {
    case Success(BatchComplete(results)) =>
      println("\nAll work completed!")
      println("Results:")
      results.toSeq.sortBy(_._1).foreach { case (id, result) =>
        println(s"Task $id: $result")
      }
      system.terminate()
      
    case Failure(ex) =>
      println(s"Processing failed: ${ex.getMessage}")
      system.terminate()
  }

  // Keep application alive  
  Thread.sleep(15000)
}