// src/main/scala/com/example/FSMDemo.scala
package com.example

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.fsm.VendingMachineActor
import com.example.fsm.VendingMachineActor._ // Import messages

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global // Important for Future combinators
import scala.util.{Failure, Success}

object FSMDemo extends App {
  val system = ActorSystem("FSMSystem")
  val vendingMachine = system.actorOf(VendingMachineActor.props(), "vendingMachine")

  implicit val timeout: Timeout = Timeout(3.seconds) // Implicit timeout for ask

  println("Starting Vending Machine scenario...\n")

  // Use a for-comprehension to sequence the asynchronous operations
  // Each 'ask' (vendingMachine ? Message) returns a Future.
  // The for-comprehension chains these Futures.
  val scenario = for {
    // Step 1: Try to request an item when out of stock
    _ <- { // The curly braces are just for grouping the println with the ask
      println("User 1: Attempting to request an item (expect SoldOut)...")
      (vendingMachine ? RequestItem).map { reply => // .map on the Future
        println(s"User 1: Received <<$reply>>\n")
      }
    }

    // Step 2: Restock the machine
    _ <- {
      println("Admin: Attempting to restock with 5 items (expect RestockSuccessful)...")
      (vendingMachine ? Restock(5)).map { reply =>
        println(s"Admin: Received <<$reply>>\n")
      }
    }

    // Step 3a: Request item 1
    _ <- {
      println("User 2: Requesting item 1 (expect ItemDispensed)...")
      (vendingMachine ? RequestItem).map(reply => println(s"User 2: Received <<$reply>>"))
    }
    // Step 3b: Request item 2
    _ <- {
      println("User 3: Requesting item 2 (expect ItemDispensed)...")
      (vendingMachine ? RequestItem).map(reply => println(s"User 3: Received <<$reply>>"))
    }
    // Step 3c: Request item 3
    _ <- {
      println("User 4: Requesting item 3 (expect ItemDispensed)...")
      (vendingMachine ? RequestItem).map(reply => println(s"User 4: Received <<$reply>>"))
    }
    // Step 3d: Request item 4
    _ <- {
      println("User 5: Requesting item 4 (expect ItemDispensed)...")
      (vendingMachine ? RequestItem).map(reply => println(s"User 5: Received <<$reply>>"))
    }
    // Step 3e: Request item 5 (last one)
    _ <- {
      println("User 6: Requesting item 5 (expect ItemDispensed)...")
      (vendingMachine ? RequestItem).map { reply =>
        println(s"User 6: Received <<$reply>>")
        println("\nMachine should now be out of stock.\n")
      }
    }

    // Step 4: Try to request another item (expect SoldOut)
    _ <- {
      println("User 7: Attempting to request an item (expect SoldOut)...")
      (vendingMachine ? RequestItem).map(reply => println(s"User 7: Received <<$reply>>\n"))
    }

    // Step 5: Try to restock with an invalid amount
    _ <- {
      println("Admin: Attempting to restock with -2 items (expect MachineError)...")
      (vendingMachine ? Restock(-2)).map(reply => println(s"Admin: Received <<$reply>>\n"))
    }

  } yield "Vending Machine scenario finished." // The final result of the successful scenario

  // Handle the completion of the entire sequence of operations
  scenario.onComplete {
    case Success(message) =>
      println(s"\n--- $message ---")
      // Terminate the actor system when the scenario is complete
      system.terminate()
    case Failure(ex) =>
      println(s"\n--- Scenario failed: ${ex.getMessage} ---")
      ex.printStackTrace()
      // Terminate the actor system even on failure
      system.terminate()
  }

  // Note: The main thread will not wait for the Futures to complete unless you add
  // Await.result, but for an App, the JVM will stay alive as long as non-daemon
  // threads (like those in Akka's default dispatcher) are running.
  // system.terminate() in onComplete is the clean way to shut down.
}