package com.akka.persistence

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.akka.persistence.CounterActorCommands._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}

object PersistenceDemoApp extends App {
  println("Starting Akka Persistence Demo...")

  val config = ConfigFactory.load() // Loads application.conf with persistence settings
  val system = ActorSystem("PersistenceSystem", config)

  implicit val timeout: Timeout = Timeout(5.seconds) // For ask pattern

  // --- Create or get reference to the PersistentCounterActor ---
  // The ID "my-counter-1" ensures it reuses the same event stream across runs.
  val counterActorId = "my-counter-1"
  val counterActor: ActorRef = system.actorOf(PersistentCounterActor.props(counterActorId), s"counterActor-$counterActorId")
  println(s"PersistentCounterActor created/referenced with ID: $counterActorId")

  def askCounter[T](command: Any): Unit = {
    (counterActor ? command).onComplete {
      case Success(response) => println(s"Response to $command: $response")
      case Failure(ex)       => println(s"Error for $command: ${ex.getMessage}")
    }
    Thread.sleep(200) // Give some time for ask to complete and log
  }
  def tellCounter(command: Any): Unit = {
    counterActor ! command
    println(s"Sent command: $command")
    Thread.sleep(100) // Give some time for processing
  }


  println("\n--- Initial Interaction ---")
  askCounter(GetCurrentValue) // Expected: 0 on first ever run, or last known value

  println("\n--- Sending Commands ---")
  askCounter(Increment(5))
  askCounter(Increment(3))
  askCounter(Decrement(2))
  askCounter(GetCurrentValue) // Expected: 6

  println("\n--- Requesting a snapshot ---")
  tellCounter(TakeSnapshotPlease) // Actor will log success/failure

  println("\n--- More Commands (post-snapshot window) ---")
  askCounter(Increment(10))
  askCounter(GetCurrentValue) // Expected: 16

  println("\n--- Printing final state (for observation) ---")
  askCounter(PrintState)


  println(s"\n--- Demo Interaction Finished ---")
  println(s"You can stop this app (Ctrl+C) and re-run it.")
  println(s"On re-run, the PersistentCounterActor with ID '$counterActorId' should recover its state from the journal.")
  println(s"Try sending different commands or stopping/restarting at different points.")
  println("\nPress ENTER to terminate the ActorSystem and exit...")
  StdIn.readLine()

  // --- Shutdown ---
  println("Terminating ActorSystem...")
  // counterActor ! PoisonPill // Let actor stop gracefully if needed for more cleanup
  system.terminate().onComplete {
    case Success(_) => println("ActorSystem terminated successfully.")
    case Failure(ex) => println(s"Error during ActorSystem termination: $ex")
  }
  println("Persistence Demo Finished.")
}