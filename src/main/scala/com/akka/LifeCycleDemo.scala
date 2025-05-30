package com.example

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.LifeCycleActor._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object LifecycleDemo extends App {
  val system = ActorSystem("LifecycleSystem")
  implicit val timeout: Timeout = Timeout(3.seconds)

  println("--- Creating LifeCycleActor ---")
  val lifecycleActor = system.actorOf(LifeCycleActor.props, "lifecycleDemoActor")

  def queryStatus(step: String): Unit = {
    (lifecycleActor ? GetStatus).mapTo[Status].onComplete {
      case Success(status) => println(s"queryStatus: DemoApp $step: Received Status: '${status.message}'")
      case Failure(ex)     => println(s"queryStatus: DemoApp $step: Failed to get status: ${ex.getMessage}")
    }
    Thread.sleep(200) // Allow ask to complete and print
  }

  println("\n--- Querying status after start ---")
  queryStatus("after start") // Actor Started

  Thread.sleep(500)

  println("\n--- Sending CauseError to trigger restart ---")
  lifecycleActor ! CauseError // This will cause an exception and restart

  // Wait for restart process to happen and logs to appear
  println("DemoApp: Waiting for actor to potentially restart (approx 5 seconds)...")
  Thread.sleep(5000)

  println("\n--- Querying status after potential restart ---")
  queryStatus("after restart") // Should be "Actor Started" again due to preStart in new instance

  Thread.sleep(500)

  println("\n--- Stopping actor gracefully using PoisonPill ---")
  lifecycleActor ! PoisonPill // A special message that tells an actor to stop itself
  // PoisonPill is queued like any other message. When processed, the actor stops.

  println("DemoApp: Waiting for actor to stop (approx 2 seconds)...")
  Thread.sleep(2000)

  println("\n--- Attempting to query status after stop (should fail or timeout) ---")
  queryStatus("after stop") // This ask should fail as the actor is stopped

  Thread.sleep(1000) // For final ask to timeout/fail
  println("DemoApp: Terminating system.")
  system.terminate()
}